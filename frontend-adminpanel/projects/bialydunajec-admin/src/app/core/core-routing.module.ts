import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {coreRoutingPaths} from './core-routing.paths';
import {PanelLayoutComponent} from './component/panel-layout/panel-layout.component';
import {CampRegistrationsModule} from '../camp-registrations/camp-registrations.module';
import {CampEditionModule} from '../camp-edition/camp-edition.module';
import {AcademicMinistryModule} from '../academic-ministry/academic-ministry.module';
import {DashboardComponent} from './component/dashboard/dashboard.component';

const coreRoutes: Routes = [
  {
    path: coreRoutingPaths.root, component: PanelLayoutComponent, children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: coreRoutingPaths.campEdition
      },
      {
        path: coreRoutingPaths.dashboard,
        component: DashboardComponent,
      },
      {
        path: coreRoutingPaths.campRegistrations,
        loadChildren: '../camp-registrations/camp-registrations.module#CampRegistrationsModule',
      },
      {
        path: coreRoutingPaths.campEdition,
        loadChildren: '../camp-edition/camp-edition.module#CampEditionModule',
      },
      {
        path: coreRoutingPaths.academicMinistry,
        loadChildren: '../academic-ministry/academic-ministry.module#AcademicMinistryModule',
      },
      {
        path: coreRoutingPaths.emailMessage,
        loadChildren: '../email-message/email-message.module#EmailMessageModule',
      },
      {
        path: coreRoutingPaths.payments,
        loadChildren: '../payments/payments.module#PaymentsModule',
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(coreRoutes)],
  exports: [RouterModule]
})
export class CoreRoutingModule {
}
