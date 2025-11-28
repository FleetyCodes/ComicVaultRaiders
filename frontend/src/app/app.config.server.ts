import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { provideServerRendering } from '@angular/platform-server';
import { provideServerRouting } from '@angular/ssr';
import { appConfig } from './app.config';
import { serverRoutes } from './app.routes.server';
import { AuthGuard } from './auth.guard';
import { NoAuthGuard } from './no.auth.guard';


const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(),
    provideServerRouting(serverRoutes),
        AuthGuard, NoAuthGuard,      
  ]
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
