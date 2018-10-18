import {Component, OnInit} from '@angular/core';
import {
  CamperEducationDto,
  CamperPersonalDataDto,
  CampParticipantRegistrationRequest
} from '../../../service/rest/request/camp-participant-registration.request';
import {AddressDto} from '../../../../../../../bialydunajec-admin/src/app/shared/service/rest/dto/address.dto';
import {CamperRegistrationFormStateService} from '../../../service/camper-registration-form-state.service';
import {CampRegistrationsEndpoint} from '../../../service/rest/camp-registrations-endpoint.service';
import {finalize} from 'rxjs/operators';
import {ErrorObserver} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {HttpResponseHelper} from '../../../../../../../bialydunajec-admin/src/app/shared/helper/HttpResponseHelper';
import {RestErrorCode} from '../../../service/rest/response/rest-error.code';

@Component({
  selector: 'bda-registration-summary',
  templateUrl: './registration-summary.component.html',
  styleUrls: ['./registration-summary.component.scss']
})
export class RegistrationSummaryComponent implements OnInit {

  lastMessage: { additionalClass: string, icon: string, header: string, content: string, };

  submittingInProgress = false;
  registeredSuccessful = false;

  showPreviousButton = false;
  showResendVerificationEmail = false;

  firstName: string;

  constructor(
    private mainFormState: CamperRegistrationFormStateService,
    private campRegistrationsEndpoint: CampRegistrationsEndpoint) {
  }

  ngOnInit() {
    this.registerCamper();
    this.firstName = this.mainFormState.getPersonalFormDataSnapshot().personalData.firstName;
  }

  private registerCamper() {
    this.submittingInProgress = true;
    this.lastMessage = null;

    const formState = this.mainFormState.getFormDataSnapshot();
    const personalDataState = formState.PERSONAL_DATA;

    const camperPersonalDataDto = new CamperPersonalDataDto(
      personalDataState.personalData.firstName,
      personalDataState.personalData.lastName,
      personalDataState.personalData.gender,
      personalDataState.personalData.pesel
    );

    const camperEducationDto = new CamperEducationDto(
      personalDataState.education.university,
      personalDataState.education.faculty,
      personalDataState.education.fieldOfStudy,
      personalDataState.education.isRecentHighSchoolGraduate,
      personalDataState.education.highSchool,
    );

    const camperAddressDto = new AddressDto(
      personalDataState.homeAddress.street,
      personalDataState.homeAddress.number,
      personalDataState.homeAddress.city,
      personalDataState.homeAddress.postalCode
    );

    const request = new CampParticipantRegistrationRequest(
      formState.COTTAGE.cottageId,
      camperPersonalDataDto,
      camperAddressDto,
      personalDataState.contact.telephone,
      personalDataState.contact.email,
      camperEducationDto
    );

    this.campRegistrationsEndpoint.registerCampParticipant(36, request)
      .pipe(
        finalize(() => this.submittingInProgress = false)
      )
      .subscribe(
        r => {
          this.registeredSuccessful = true;
          this.lastMessage = {
            additionalClass: 'success',
            icon: 'check circle outline icon',
            header: 'Zapisaliśmy Ciebie na Obóz!',
            content: 'Właśnie na Twojego maila wysłaliśmy wiadomość (prosimy, sprawdź czy dojdzie w ciągu 5 minut). Prosimy o szczegółowe zapoznanie się z jej treścią, a także o kliknięcie w ciągu trzech dni w przesłany link potwierdzający, Twój zapis na Obóz. Jeśli tego nie zrobisz, to po tym czasie Twoje miejsce skreślimy komuś innemu.'
          };
        },
        new RequestErrorObserverBuilder(
          (restErrors: string[]) => {
            if (restErrors.includes(RestErrorCode.CAMP_PARTICIPANT_WITH_GIVEN_PESEL_IS_ALREADY_REGISTERED)) {
              this.lastMessage = {
                additionalClass: 'negative',
                icon: 'times circle outline icon',
                header: 'Jesteś już zapisany/-a na Obóz!',
                content: 'Wygląda na to, że zapisałeś/-aś się już wcześniej. Jeśli tego nie zrobiłeś/-aś, skontaktuj się z administratorem.'
              };
              this.showResendVerificationEmail = true;
            } else if (restErrors.includes(RestErrorCode.COTTAGE_NOT_FOUND)) {
              this.lastMessage = {
                additionalClass: 'negative',
                icon: 'times circle outline icon',
                header: 'Brak miejsca w wybranej chatce!',
                content: 'Wygląda na to, że w międzyczasie ktoś Cię wyprzedził :( Jeśli bardzo chcesz zapisać się do tej chatki, spytaj jej szefa o taką możliwość.'
              };
              this.showPreviousButton = true;
            } else if (restErrors.includes(RestErrorCode.CAMP_EDITION_HAS_NOT_IN_PROGRESS_REGISTRATIONS)) {
              this.lastMessage = {
                additionalClass: 'negative',
                icon: 'times circle outline icon',
                header: 'Zapisy na Obóz są nieaktywne!',
                content: 'Niestety zapisy na Obóz są teraz nieaktywne :( Spróbuj ponownie później.'
              };
            }
          },
          unhandledError => {
            this.lastMessage = {
              additionalClass: 'negative',
              icon: 'times circle outline icon',
              header: 'Błąd!',
              content: 'Niestety...'
            };
          },
          networkError => {
            this.lastMessage = {
              additionalClass: 'negative',
              icon: 'times circle outline icon',
              header: 'Błąd!',
              content: 'Niestety...'
            };
          }
        ).getRequestErrorObserver()
      );
  }


}

export class RequestErrorObserverBuilder {
  restError: (restErrors: string[] | RestErrorCode[]) => any;
  networkError: (error) => any;
  unhandledError: (error) => any;

  private static defaultCallback = error => console.log(error);

  constructor(
    restError: (restErrors: string[] | RestErrorCode[]) => any = RequestErrorObserverBuilder.defaultCallback,
    unhandledError: (error) => any = RequestErrorObserverBuilder.defaultCallback,
    networkError: (error) => any = RequestErrorObserverBuilder.defaultCallback) {
    this.restError = restError;
    this.networkError = networkError;
    this.unhandledError = unhandledError;
  }

  getRequestErrorObserver() {
    return (response: HttpErrorResponse) => {
      const error = response.error;
      const restErrors = response.error.restErrors;
      if (HttpResponseHelper.isStatus4xx(response) && restErrors) {
        console.log('REST ERROR');
        this.restError(restErrors);
      } else if (response.status === 0) {
        console.log('NETWORK ERROR');
        this.networkError(error);
      } else {
        console.log('UNHANDLED ERROR');
        this.unhandledError(error);
      }
    };
  }
}




