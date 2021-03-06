import {AbstractEndpoint} from '../../../shared/service/rest/abstract.endpoint';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '../../../auth/service/auth.service';
import {CampEditionResponse} from '../../../camp-edition/service/rest/response/camp-edition.response';
import {Injectable} from '@angular/core';
import {CampRegistrationsEditionResponse} from './response/camp-registrations-edition.response';
import {UpdateCampRegistrationsTimerRequest} from './request/update-camp-registrations-timer.request';
import {AcademicMinistryResponse} from './response/academic-ministry.response';
import {CottageResponse} from './response/cottage.response';
import {UpdateCottageRequest} from './request/update-cottage.request';
import {PageDto} from './dto/page.dto';
import {CampParticipantResponse} from './response/camp-participant.response';
import {environment} from '../../../../environments/environment';
import {ShirtOrderReadModel} from './response/shirt-order.read-model';

@Injectable({
  providedIn: 'root'
})
export class CampRegistrationsEndpoint extends AbstractEndpoint {

  constructor(httpClient: HttpClient, authService: AuthService) {
    super(httpClient, authService, '/rest-api/v1/admin/camp-registrations');
  }

  updateCampRegistrationsTimerById(campRegistrationsEditionId: number, requestBody: UpdateCampRegistrationsTimerRequest) {
    return this.httpClient.patch(`${this.callsBaseUrl}/${campRegistrationsEditionId}/timer`, requestBody);
  }

  startCampRegistrationsEditionById(campRegistrationsEditionId: number) {
    return this.httpClient.patch(`${this.callsBaseUrl}/${campRegistrationsEditionId}/start`, null);
  }

  suspendCampRegistrationsEditionById(campRegistrationsEditionId: number) {
    return this.httpClient.patch(`${this.callsBaseUrl}/${campRegistrationsEditionId}/suspend`, null);
  }

  unsuspendCampRegistrationsEditionById(campRegistrationsEditionId: number) {
    return this.httpClient.patch(`${this.callsBaseUrl}/${campRegistrationsEditionId}/unsuspend`, null);
  }

  finishCampRegistrationsEditionById(campRegistrationsEditionId: number) {
    return this.httpClient.patch(`${this.callsBaseUrl}/${campRegistrationsEditionId}/finish`, null);
  }

  getAllCampRegistrations() {
    return this.httpClient.get<CampRegistrationsEditionResponse[]>(`${this.callsBaseUrl}`);
  }

  getCampRegistrationsEditionById(campRegistrationsEditionId: number) {
    return this.httpClient.get<CampRegistrationsEditionResponse>(`${this.callsBaseUrl}/${campRegistrationsEditionId}`);
  }

  getAllCampEditions() {
    return this.httpClient.get<CampEditionResponse[]>(`${this.callsBaseUrl}/camp-edition`);
  }

  getCampEditionByCampRegistrationsEditionId(campRegistrationsEditionId: number) {
    return this.httpClient.get<CampEditionResponse>(`${this.callsBaseUrl}/${campRegistrationsEditionId}/camp-edition`);
  }

  getAllAcademicMinistries() {
    return this.httpClient.get<AcademicMinistryResponse[]>(`${this.callsBaseUrl}/academic-ministry`);
  }

  getAcademicMinistryById(academicMinistryId: string) {
    return this.httpClient.get<AcademicMinistryResponse>(`${this.callsBaseUrl}/academic-ministry/${academicMinistryId}`);
  }

  createAcademicMinistryCottage(campRegistrationsEditionId: number, academicMinistryId: string) {
    return this.httpClient.post(
      `${this.callsBaseUrl}/cottage/academic-ministry-cottage?campRegistrationsEditionId=${campRegistrationsEditionId}&academicMinistryId=${academicMinistryId}`
      , null
    );
  }

  createStandaloneCottage(campRegistrationsEditionId: number, cottageName: string) {
    return this.httpClient.post(
      `${this.callsBaseUrl}/cottage/standalone-cottage?campRegistrationsEditionId=${campRegistrationsEditionId}&cottageName=${cottageName}`,
      null
    );
  }

  updateCottage(cottageId: string, requestBody: UpdateCottageRequest) {
    return this.httpClient.put(
      `${this.callsBaseUrl}/cottage/${cottageId}`,
      requestBody
    );
  }

  deleteCottage(cottageId: string) {
    return this.httpClient.delete(
      `${this.callsBaseUrl}/cottage/${cottageId}`
    );
  }

  activateCottage(cottageId: string) {
    return this.httpClient.patch(
      `${this.callsBaseUrl}/cottage/${cottageId}/activate`,
      null
    );
  }

  deactivateCottage(cottageId: string) {
    return this.httpClient.patch(
      `${this.callsBaseUrl}/cottage/${cottageId}/deactivate`,
      null
    );
  }

  getAllCottagesByCampRegistrationsEditionId(campRegistrationsEditionId: number) {
    return this.httpClient.get<CottageResponse[]>(`${this.callsBaseUrl}/cottage?campRegistrationsEditionId=${campRegistrationsEditionId}`);
  }

  getCottagesByIdAndCampRegistrationsEditionId(cottageId: string, campRegistrationsEditionId: number) {
    return this.httpClient.get<CottageResponse>(`${this.callsBaseUrl}/cottage/${cottageId}?campRegistrationsEditionId=${campRegistrationsEditionId}`);
  }

  countCampParticipantsByCottageId(cottageId: string) {
    return this.httpClient.get<{ cottageId: string, campParticipantsCount: number }>(`${this.callsBaseUrl}/camp-participant/count?cottageId=${cottageId}`);
  }

  getPageOfAllCampParticipants(page: number, size: number) {
    return this.httpClient.get<PageDto<CampParticipantResponse>>(`${this.callsBaseUrl}/camp-participant?page=${page}&size=${size}`);
  }

  getPageOfCampParticipantsByCampRegistrationsEditionId(campRegistrationsEditionId: number, page: number, size: number) {
    return this.httpClient.get<PageDto<CampParticipantResponse>>(
      `${this.callsBaseUrl}/camp-participant?campRegistrationsEditionId=${campRegistrationsEditionId}&page=${page}&size=${size}`
    );
  }

  getCampParticipantsByCampRegistrationsEditionId(campRegistrationsEditionId: number | string) {
    return this.httpClient.get<CampParticipantResponse[]>(
      `${environment.restApi.baseUrl}/rest-api/v1/admin/camp-participant?campRegistrationsEditionId=${campRegistrationsEditionId}`
    );
  }

  getCampParticipantById(campParticipantId: string) {
    return this.httpClient.get<CampParticipantResponse[]>(
      `${environment.restApi.baseUrl}/rest-api/v1/admin/camp-participant/${campParticipantId}`
    );
  }

  getCampEditionShirt(campRegistrationsEditionId: number | string) {
    return this.httpClient.get<CottageResponse>(`${this.callsBaseUrl}/camp-shirt/?campRegistrationsEditionId=${campRegistrationsEditionId}`);
  }

  getCampRegistrationsStatisticsByCampRegistrationsEditionId(campRegistrationsEditionId: number | string) {
    return this.httpClient.get<any>(
      `${environment.restApi.baseUrl}/rest-api/v1/admin/camp-registrations-statistics/${campRegistrationsEditionId}`
    );
  }

  getShirtOrdersByCampRegistrationsEditionId(campRegistrationsEditionId: number | string) {
    return this.httpClient.get<ShirtOrderReadModel[]>(
      `${environment.restApi.baseUrl}/rest-api/v1/admin/shirt-order?campRegistrationsEditionId=${campRegistrationsEditionId}`
    );
  }

  verifyCampParticipantRegistrationByAuthorized(campParticipantRegistrationId: string) {
    return this.httpClient.patch(
      `${environment.restApi.baseUrl}/rest-api/v1/admin/camp-registrations/camp-participant-registration/${campParticipantRegistrationId}/verification`,
      {}
    );
  }
}
