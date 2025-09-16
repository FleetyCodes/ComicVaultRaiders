import { Component, OnInit } from '@angular/core';
import { HelloService } from './hello.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'library-of-solitude';
  helloMessage: string = 'Betöltés...';

  constructor(private helloService: HelloService) {}

  ngOnInit() {
    this.helloService.getHello().subscribe({
      next: (response) => {
        this.helloMessage = response;
      },
      error: (err) => {
        console.error('Hiba történt a hívás során:', err);
        this.helloMessage = 'Hiba a hívás során.';
      }
    });
  }
}
