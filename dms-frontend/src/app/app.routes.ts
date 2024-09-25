import { Routes } from '@angular/router';
import {DocumentsComponent} from "./components/documents/documents.component";

export const routes: Routes = [
  {
    path: "documents",
    component: DocumentsComponent
  },
  {
    path: "**",
    redirectTo: "/documents"
  }
];
