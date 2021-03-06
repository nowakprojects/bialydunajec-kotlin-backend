import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {campersRegistrationRoutingPaths} from './campers-registration-routing.paths';
import {RegistrationFormComponent} from './component/registration-form/registration-form.component';
import {PersonalDataFormComponent} from './component/registration-form/personal-data-form/personal-data-form.component';
import {TransportFormComponent} from './component/registration-form/transport-form/transport-form.component';
import {ShirtFormComponent} from './component/registration-form/shirt-form/shirt-form.component';
import {CottageFormComponent} from './component/registration-form/cottage-form/cottage-form.component';
import {RegistrationSummaryComponent} from './component/registration-form/registration-summary/registration-summary.component';
import {RegistrationStartComponent} from './component/registration-start/registration-start.component';
import {InProgressCampRegistrationsGuard} from './service/in-progress-camp-registrations.guard';
import {RegistrationVerificationComponent} from './component/registration-verification/registration-verification.component';

const campersRegistrationRoutes: Routes = [
  {
    path: campersRegistrationRoutingPaths.root,
    redirectTo: campersRegistrationRoutingPaths.start,
    pathMatch: 'full'
  },
  {
    path: campersRegistrationRoutingPaths.start,
    component: RegistrationStartComponent
  },
  {
    path: campersRegistrationRoutingPaths.verification,
    component: RegistrationVerificationComponent
  },
  {
    path: campersRegistrationRoutingPaths.form,
    component: RegistrationFormComponent,
    canActivate: [InProgressCampRegistrationsGuard],
    canActivateChild: [InProgressCampRegistrationsGuard],
    children: [
      {path: '', pathMatch: 'full', redirectTo: campersRegistrationRoutingPaths.personalData},
      {path: campersRegistrationRoutingPaths.personalData, component: PersonalDataFormComponent},
      {path: campersRegistrationRoutingPaths.transport, component: TransportFormComponent},
      {path: campersRegistrationRoutingPaths.shirt, component: ShirtFormComponent},
      {path: campersRegistrationRoutingPaths.cottage, component: CottageFormComponent},
      {path: campersRegistrationRoutingPaths.summary, component: RegistrationSummaryComponent}
    ]
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(campersRegistrationRoutes)
  ],
  exports: [
    RouterModule
  ]
})
export class CampersRegistrationRoutingModule {
}
