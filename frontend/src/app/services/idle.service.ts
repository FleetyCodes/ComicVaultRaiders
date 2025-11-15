import { inject, Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Idle, DEFAULT_INTERRUPTSOURCES } from '@ng-idle/core';
import { loginDialog } from '../components/login-dialog/login-dialog';

@Injectable({ providedIn: 'root' })

export class IdleService {  
  private dialog = inject(MatDialog);

  constructor(private idle: Idle, private router: Router) {
    // available inactivity time before timeout
    // this should around 1800 sec
    this.idle.setIdle(1800);

    // immidiate timeout
    this.idle.setTimeout(1);

    // idle reset activities
    this.idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);

    this.idle.onTimeout.subscribe(() => {
      this.IdleTimeOver();
    });

    
  }

  private IdleTimeOver() {
        const dialogRef = this.dialog.open(loginDialog, {
          disableClose: true,
    });
  }

  public stopIdleTimer() {
    this.idle.stop();
  }

  public startIdleTimer() {
    this.idle.watch();
  }




}
