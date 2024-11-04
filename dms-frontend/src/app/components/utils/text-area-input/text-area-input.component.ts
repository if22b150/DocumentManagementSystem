import {Component, Input} from '@angular/core';
import {AbstractControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MdbFormsModule} from "mdb-angular-ui-kit/forms";
import {NgIf} from "@angular/common";
import {MdbValidationModule} from "mdb-angular-ui-kit/validation";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-text-area-input',
  standalone: true,
  imports: [
    FormsModule,
    MdbFormsModule,
    ReactiveFormsModule,
    NgIf,
    MdbValidationModule,
    TranslateModule,
  ],
  templateUrl: './text-area-input.component.html',
  styleUrl: './text-area-input.component.scss',
})
export class TextAreaInputComponent {
  @Input({ required: true }) id!: string;
  @Input({ required: true }) label!: string;
  @Input({ required: true }) controlName!: string;
  @Input({ required: true }) formGroup!: FormGroup;
  @Input() isRequired: boolean = false;
  @Input() rows: number = 4;
  @Input() placeholder: string | null = null;

  get control(): AbstractControl | null {
    return this.formGroup!.get(this.controlName!);
  }
}
