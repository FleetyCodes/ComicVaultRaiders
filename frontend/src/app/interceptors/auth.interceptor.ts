import { HttpInterceptorFn } from '@angular/common/http';
import { catchError, switchMap, tap, throwError } from 'rxjs';
import { UserService } from '../services/user.service';
import { inject } from '@angular/core';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

    const userService = inject(UserService);
    const includeEndpoints = [
        'v1/comic',
        'v1/comic/exceptUser',
        'v1/user/comics/',
        'v1/user/comics'
    ];

    const shouldIntercept = includeEndpoints.some(endpoint =>
        req.url.includes(endpoint)
    );

    if (!shouldIntercept) {
    return next(req);
  }

  return next(req).pipe(
    catchError(error => {
      if (error.status === 401) {
        return userService.refreshJwtToken().pipe(
          switchMap(response => {
            userService.setToken(response.token);
            const clonedReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${response.token}`
              }
            });
            return next(clonedReq);
          }),
          catchError(err => {
            //TODO: retry 3 times before logging out
            return throwError(() => err);
          })
        );
      }
      return throwError(() => error);
    })
  );
};