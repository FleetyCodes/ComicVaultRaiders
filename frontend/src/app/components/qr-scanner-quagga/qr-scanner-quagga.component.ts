import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnDestroy, OnInit, Output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import Quagga from '@ericblade/quagga2';



@Component({
  selector: 'app-qr-scanner-quagga',
  templateUrl: './qr-scanner-quagga.component.html',
  styleUrls: ['./qr-scanner-quagga.component.scss'],
  imports: [CommonModule, MatInputModule, MatButtonModule],
})
export class QrScannerQuaggaComponent implements OnInit, OnDestroy {
  @Output() scanned = new EventEmitter<string>();
  protected barcode = signal<string>("");

  ngOnInit() {
    Quagga.init({
      inputStream: {
        type: 'LiveStream',
        target: '#interactive',
        constraints: {
          facingMode: 'environment',
          width: { ideal: 1280 },
          height: { ideal: 720 },
        }
      },
      decoder: {
        readers: [
          'code_128_reader',
          'ean_reader', 
          'ean_5_reader', 
          'ean_2_reader', 
          'ean_8_reader', 
          'code_39_reader', 
          'code_39_vin_reader', 
          'codabar_reader', 
          'upc_reader', 
          'upc_e_reader', 
          'i2of5_reader', 
          '2of5_reader', 
          'code_93_reader', 
          'code_32_reader']
      },
      locate: true
    },
      err => {
        Quagga.start();
      }),

      Quagga.onDetected(result => {
        if(result.codeResult.code){
          this.barcode.set(result.codeResult.code);
        }
      });

    Quagga.onProcessed(() => {
      const video: HTMLVideoElement | null = document.querySelector('#interactive video');
      if (video) {
        video.style.width = '200px';
        video.style.height = 'auto';
        video.style.objectFit = 'cover';
        video.style.borderRadius = '12px';
      }

      const canvases = document.querySelectorAll<HTMLCanvasElement>('.drawingBuffer');
      canvases.forEach(canvas => {
        canvas.style.width = '200px';
        canvas.style.height = '40px';
      });
    });

  }

  
  ngAfterViewInit() {
    setTimeout(() => {
      const video: HTMLVideoElement | null = document.querySelector('#interactive video');
      if (video) {
        video.style.width = '200px';
        video.style.height = 'auto';
        video.style.objectFit = 'cover';
        video.style.borderRadius = '12px';
      }
    }, 500);
  }


  ngOnDestroy() {
    Quagga.stop();
    Quagga.offDetected(() => { });
  }

  stopScan() {
    Quagga.stop();
    this.scanned.emit(this.barcode());
  }
}