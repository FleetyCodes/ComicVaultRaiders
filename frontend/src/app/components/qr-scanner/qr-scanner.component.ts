import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { BarcodeFormat } from '@zxing/library';
import { ZXingScannerModule } from '@zxing/ngx-scanner';


@Component({
  selector: 'app-qr-scanner',
  templateUrl: './qr-scanner.component.html',
  styleUrls: ['./qr-scanner.component.scss'],
  imports: [ZXingScannerModule, CommonModule],
})
export class QrScannerComponent {
  @Output() scanned = new EventEmitter<string>();

  torchOn = false;

  allowedFormats = [
    BarcodeFormat.QR_CODE,
    BarcodeFormat.EAN_13,
    BarcodeFormat.EAN_8,
    BarcodeFormat.CODE_128,
    BarcodeFormat.CODE_39,
    BarcodeFormat.CODE_93,
    BarcodeFormat.ITF,
    BarcodeFormat.UPC_A,
    BarcodeFormat.UPC_E
  ];

  onScan(result: string) {
    console.log('Scanned code:', result);
    this.scanned.emit(result);
  }

  onFail(err: any) {
    console.log('Failed to scan:', err);
  }
}
