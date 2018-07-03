import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { EntityAuditComponent } from './entity-audit.component';

export const entityAuditRoute: Route =
    {
        path: 'entity-audit',
        component: EntityAuditComponent,
        data: {
            pageTitle: 'global.menu.admin.entity-audit'
        }
    };

// @NgModule({
//     imports: [RouterModule.forChild(routes)],
//     exports: [RouterModule]
// })
// export class EntityAuditRoutingModule { }
