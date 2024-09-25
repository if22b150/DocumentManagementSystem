import {Component, input} from '@angular/core';
import {AbstractControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MdbFormsModule} from "mdb-angular-ui-kit/forms";
import {MdbValidationModule} from "mdb-angular-ui-kit/validation";
import {NgIf} from "@angular/common";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-form-input',
  standalone: true,
  imports: [
    FormsModule,
    MdbFormsModule,
    MdbValidationModule,
    NgIf,
    ReactiveFormsModule,
    TranslateModule
  ],
  templateUrl: './form-input.component.html',
  styleUrl: './form-input.component.scss'
})
export class FormInputComponent {
  id= input.required<string>();
  label= input.required<string>();
  controlName = input.required<string>();
  formGroup = input.required<FormGroup>()
  isRequired = input(false)
  type = input('text')
  placeholder = input<string>()
  minLength = input<number>()
  min = input<number>()

  get control(): AbstractControl | null {
    return this.formGroup().get(this.controlName())
  }
}
