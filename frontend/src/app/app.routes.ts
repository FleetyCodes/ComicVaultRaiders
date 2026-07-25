import { Routes } from "@angular/router";
import { AuthGuard } from "./auth.guard";
import { NoAuthGuard } from "./no.auth.guard";
import { HowToUseComponent } from "./pages/how-to-use-page/how-to-use.component";
import { LandingPageComponent } from "./pages/landing-page/landing-page.component";
import { LoggedInPageComponent } from "./pages/logged-in-page/logged-in";
import { LoginComponent } from "./pages/login-page/login.component";
import { MyComicsPageComponent } from "./pages/my-comics/my-comics";
import { RegisterComponent } from "./pages/registration-page/registration.component";
import { SettingsScreenComponent } from "./pages/settings-page/settings-screen";
import { temporaryUnavailableComponent } from "./pages/wip-page/temporary-unavailable";
import { WishlistedComicsPageComponent } from "./pages/wishlist-page/wishlist.component";


export const routes: Routes = [
  { path: '', component: LandingPageComponent, canActivate: [NoAuthGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [NoAuthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [NoAuthGuard] }, 
  { path: 'logged-in', component: LoggedInPageComponent, canActivate: [AuthGuard] }, 
  { path: 'my-comics', component: MyComicsPageComponent, canActivate: [AuthGuard] },
  { path: 'app-work-in-progress', component: temporaryUnavailableComponent,},
  { path: 'wishlist-page', component: WishlistedComicsPageComponent, canActivate: [AuthGuard]},
  { path: 'settings', component: SettingsScreenComponent, canActivate : [AuthGuard] },
  { path: 'how-to-use', component: HowToUseComponent,  },

  //{ path: '**', redirectTo: '', pathMatch: 'full' }   // fallback
];