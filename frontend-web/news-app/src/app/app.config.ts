import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideHttpClient} from "@angular/common/http";
import {HttpClientInMemoryWebApiModule} from "angular-in-memory-web-api";
import {InmemoryDataService} from "./shared/services/inmemory.data.service";

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    provideRouter(routes),
    importProvidersFrom(HttpClientInMemoryWebApiModule.forRoot(InmemoryDataService))
  ]
};
