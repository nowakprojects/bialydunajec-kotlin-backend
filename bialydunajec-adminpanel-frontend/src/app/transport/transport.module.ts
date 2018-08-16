import {NgModule} from '@angular/core';
import {SharedModule} from '../shared/shared.module';
import {FlexLayoutModule} from '@angular/flex-layout';
import {TransportComponent} from './component/transport/transport.component';
import {TransportRoutingModule} from './transport-routing.module';
import { MeanOfTransportComponent } from './component/mean-of-transport/mean-of-transport.component';
import { MeanOfTransportHeaderComponent } from './component/mean-of-transport-header/mean-of-transport-header.component';
import { TransportInfoCampBusComponent } from './component/transport-info-camp-bus/transport-info-camp-bus.component';
import { TransportInfoCarComponent } from './component/transport-info-car/transport-info-car.component';

@NgModule({
  imports: [
    SharedModule,
    FlexLayoutModule,
    TransportRoutingModule,
    SharedModule
  ],
  declarations: [TransportComponent, MeanOfTransportComponent, MeanOfTransportHeaderComponent, TransportInfoCampBusComponent, TransportInfoCarComponent]
})
export class TransportModule {
}
