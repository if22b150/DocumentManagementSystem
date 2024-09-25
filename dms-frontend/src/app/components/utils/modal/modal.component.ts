import {Component, input, output} from '@angular/core';
import {MdbModalRef} from "mdb-angular-ui-kit/modal";
import {MdbButtonComponent} from "../mdb-button/mdb-button.component";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [
    MdbButtonComponent,
    TranslateModule
  ],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.scss'
})
export class ModalComponent {
  title = input.required<string>();
  submitText = input("SUBMIT");
  closeText = input("CLOSE");
  loading = input(false);
  submitClicked = output();

  constructor(public modalRef: MdbModalRef<ModalComponent>) {}
}
