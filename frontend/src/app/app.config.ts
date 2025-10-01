import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { AuthGuard } from './auth.guard';
import { NoAuthGuard } from './no.auth.guard';
import { Idle, IdleExpiry, SimpleExpiry } from '@ng-idle/core';
import { Keepalive } from '@ng-idle/keepalive';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    provideHttpClient(withFetch()),
    importProvidersFrom(
      BrowserAnimationsModule,
      MatButtonModule
    ),
    AuthGuard, NoAuthGuard,
     Idle,
    Keepalive,
    { provide: IdleExpiry, useClass: SimpleExpiry  },
  ],
};