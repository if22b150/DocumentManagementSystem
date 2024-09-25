import {Component, effect, input, numberAttribute, output, signal} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {MdbScrollbarModule} from "mdb-angular-ui-kit/scrollbar";
import {MdbTableModule} from "mdb-angular-ui-kit/table";
import {FormInputComponent} from "../form-input/form-input.component";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {
  CurrencyPipe,
  DatePipe,
  DecimalPipe,
  KeyValuePipe,
  NgForOf,
  NgIf,
  NgOptimizedImage, NgTemplateOutlet,
  TitleCasePipe
} from "@angular/common";
import {RouterLink} from "@angular/router";
import {MdbButtonComponent} from "../mdb-button/mdb-button.component";
import {SelectorModel, TableButtonModel, TableColumnModel, TableLoadingModel} from "../../../models/table-column.model";
import {MdbCheckboxModule} from "mdb-angular-ui-kit/checkbox";
import {MdbFormsModule} from "mdb-angular-ui-kit/forms";
import {MdbSelectModule} from "mdb-angular-ui-kit/select";
import {RequestOptions} from "../../../models/request-options.model";
import {CheckboxInputComponent} from "../checkbox-input/checkbox-input.component";
import {StarRatingComponent} from "../star-rating/star-rating.component";

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MdbScrollbarModule,
    MdbTableModule,
    FormInputComponent,
    TranslateModule,
    NgForOf,
    TitleCasePipe,
    RouterLink,
    MdbButtonComponent,
    NgIf,
    MdbCheckboxModule,
    CheckboxInputComponent,
    DatePipe,
    NgOptimizedImage,
    MdbFormsModule,
    MdbSelectModule,
    KeyValuePipe,
    StarRatingComponent,
    DecimalPipe,
    CurrencyPipe,
    NgTemplateOutlet
  ],
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss',
})
export class TableComponent {
  columns = input.required<TableColumnModel[]>();
  data = input.required<any[]>();
  filterFormGroup = input.required<FormGroup>();
  selectable = input(false);
  loading = input<TableLoadingModel>({loading: false});
  hasActiveFilters = input<boolean>();
  deleteLoadingIds = input<number[]>([])

  selectionChange = output<any[]>();
  doFilterRequest = output<RequestOptions | undefined>();
  urlParamsChange = output<string | undefined>()

  selections = signal<any[]>([])
  hideTableBody: boolean = false

  constructor(private formBuilder: FormBuilder,
              private translate: TranslateService) {
    effect(() => {
      this.hideTableBody = this.loading().loading && !this.loading().lazyLoad && (this.loading().hasFilters || this.hasActiveFilters() || this.loading().isPaginationChange == true)
    });

    effect(() => {
      this.selectionChange.emit(this.selections());
    });

    effect(() => {
      if (Object.keys(this.filterFormGroup()!.controls).length > 0) {
        this.filterFormGroup()!.valueChanges.subscribe(() => {
          let urlParams = this.getFilterUrlParams();
          this.urlParamsChange.emit(urlParams != '' ? urlParams : undefined)
          this.doFilterRequest.emit({
            urlParams: urlParams != '' ? urlParams : undefined
          });
        });
      }
    });
  }

  getControlName(c: AbstractControl): string | null {
    return (
      Object.keys(c.parent!.controls).find(
        (name) => c === c.parent!.get(name),
      ) || null
    );
  }

  // selector could be just ['address'], but also ['address.street', 'address.postcode']
  getData(
    selector: SelectorModel[] | null,
    data: any,
    selectorIsId: boolean = false,
  ): string {
    if (selectorIsId) selector = [{selector: 'id'}];
    else if (!selector) return '';

    let result = '';
    selector.forEach((s: SelectorModel) => {
      let splits = s.selector.split('.');
      let selectorResult = data;
      splits.forEach((sp) => {
        selectorResult = selectorResult[sp];
      });

      if (s.isBoolean) {
        if (selectorResult === true) {
          result += this.translate.instant(s.booleanTrueText ?? '')
        } else
          result += ''
      } else
        result += selectorResult;
      if (s.suffix) result += s.suffix;
    });
    return result;
  }

  castToString(value: any): string {
    return value as string;
  }

  getEnumValue(name: string, enumType: any) {

  }

  select(data: any) {
    this.selections.update((selections) => {
      return [...selections, data];
    });
  }

  selectAll() {
    this.selections.set(this.data());
  }

  unselect(data: any) {
    this.selections.update((selections) => {
      return selections.filter((s) => s.id !== data.id);
    });
  }

  unselectAll() {
    if (!this.isAllSelected()) return;
    this.selections.set([]);
  }

  isSelected(data: any) {
    return this.selections().find((s) => s.id === data.id) != null;
  }

  isAllSelected() {
    let x = true;
    for (const d of this.data()) {
      if (!this.selections().find((s) => s.id == d.id)) {
        x = false;
        break;
      }
    }
    return x;
  }

  getFilterUrlParams() {
    let params = '';
    Object.keys(this.filterFormGroup()!.controls).forEach((key, i) => {
      const value = this.filterFormGroup()!.get(key)?.value; // Access the control value
      if (value) {
        if (params != '')
          params += '&';
        let paramKey = this.columns().find(c => c.filterFormControl && this.getControlName(c.filterFormControl) == key)?.filterUrlParam
        params += `${paramKey}=${value}`;
      }
    });

    return params;
  }

  paginationPrevious() {
    let urlParams = this.getFilterUrlParams();
    this.doFilterRequest.emit({urlParams: urlParams != '' ? urlParams : undefined})
  }

  paginationNext() {
    let urlParams = this.getFilterUrlParams();
    this.doFilterRequest.emit({urlParams: urlParams != '' ? urlParams : undefined})
  }

  btnLoading(btn: TableButtonModel, id: number) {
    return btn.loading || (btn.isDelete && this.deleteLoadingIds().indexOf(id) !== -1)
  }

  protected readonly Object = Object;
  protected readonly numberAttribute = numberAttribute;
}
