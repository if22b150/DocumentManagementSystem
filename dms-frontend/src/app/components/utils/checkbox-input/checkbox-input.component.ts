import {Component, Input} from '@angular/core';
import {MdbCheckboxModule} from "mdb-angular-ui-kit/checkbox";
import {AbstractControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {MdbFormsModule} from "mdb-angular-ui-kit/forms";
import {MdbValidationModule} from "mdb-angular-ui-kit/validation";

@Component({
  selector: 'app-checkbox-input',
  standalone: true,
  imports: [
    MdbCheckboxModule,
    NgIf,
    ReactiveFormsModule,
    MdbFormsModule,
    MdbValidationModule
  ],
  templateUrl: './checkbox-input.component.html',
  styleUrl: './checkbox-input.component.scss'
})
export class CheckboxInputComponent {
  @Input({required: true}) id!: string;
  @Input({required: true}) label!: string;
  @Input({required: true}) controlName!: string;
  @Input({required: true}) formGroup!: FormGroup;
  @Input() isRequiredTrue: boolean = false;

  get control(): AbstractControl | null {
    return this.formGroup!.get(this.controlName!)
  }
}
