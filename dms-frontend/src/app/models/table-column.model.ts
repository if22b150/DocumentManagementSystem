import {AbstractControl} from "@angular/forms";

export interface TableColumnModel {
  name?: string
  selector?: SelectorModel[]
  arraySelector?: string
  filterFormControl?: AbstractControl | null
  filterFormControlLabel?: string
  filterUrlParam?: string
  isCheckbox?: boolean
  buttons?: TableButtonModel[]
  isDate?: boolean
  isImage?: boolean
  isRating?: boolean
  isPrice?: boolean
  suffix?: string
  enum?: any
}

export interface TableButtonModel {
  text?: string
  iconClass?: string
  iconPosRight?: boolean
  color?: string
  size?: string
  outlined?: boolean
  floating?: boolean
  customClasses?: string
  routerLink?: string
  loading?: boolean
  isObjectLink?: boolean
  objectBaseLink?: string
  onClick?: (...args: any[]) => void,
  isDelete?: boolean
}

export interface SelectorModel {
  selector: string
  suffix?: string
  booleanTrueText?: string
  isBoolean?: boolean
}

export interface TableLoadingModel {
  loading: boolean
  hasFilters?: boolean
  isPaginationChange?: boolean
  lazyLoad?: boolean
}
