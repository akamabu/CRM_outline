export const routes = [
  { path: '', redirectTo: 'leads', pathMatch: 'full' },
  { path: 'leads', loadComponent: () => import('./features/leads/lead-inbox.component').then((m) => m.LeadInboxComponent) },
  { path: 'quotations', loadComponent: () => import('./features/quotations/quotation-list.component').then((m) => m.QuotationListComponent) }
];
