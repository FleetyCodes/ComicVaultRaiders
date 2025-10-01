import { Component, OnInit } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {FormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { IdleService } from '../../services/idle.service';

@Component({
  selector: 'logged-in-page',
  templateUrl: './logged-in.html',
  styleUrls: ['./logged-in.scss'],  
  standalone: true,
  imports: [
    MatToolbarModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    CommonModule,
    RouterModule
  ],
  
})


export class LoggedInPageComponent implements OnInit {

constructor(private idleService: IdleService) { } 

  title = 'Comic Vault Raiders';

  ngOnInit() {
    this.idleService.startIdleTimer();
  }


}

