import { Routes } from '@angular/router';
import {DocumentsComponent} from "./components/documents/documents.component";
import {DocumentComponent} from "./components/documents/document/document.component";

export const routes: Routes = [
  {
    path: "documents",
    component: DocumentsComponent
  },
  {
    path: "documents/:id",
    component: DocumentComponent
  },
  {
    path: "**",
    redirectTo: "/documents"
  }
];
