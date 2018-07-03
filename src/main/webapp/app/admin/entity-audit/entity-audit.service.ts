import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { EntityAuditEvent } from './entity-audit-event.model';

@Injectable()
export class EntityAuditService {

    constructor(private http: HttpClient) { }

    getAllAudited(): Observable<string[]> {
        return this.http.get<string[]>('api/audits/entity/all');
    }

    findByEntity(entity: string, limit: number): Observable<EntityAuditEvent[]> {
        return this.http.get<EntityAuditEvent[]>(
            'api/audits/entity/changes',
            {
                params: {
                    entityType: entity,
                    limit: limit.toString()
                }
            }
        );
    }

    getPrevVersion(qualifiedName: string, entityId: string, commitVersion: number): Observable<EntityAuditEvent> {
        return this.http.get<EntityAuditEvent>(
            'api/audits/entity/changes/version/previous',
            {
                params: {
                    qualifiedName: qualifiedName,
                    entityId: entityId,
                    commitVersion:commitVersion.toString()
                }
            });
    }
}
