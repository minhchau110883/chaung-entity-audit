import { BaseEntity } from './../../shared';

export class Person implements BaseEntity {
    constructor(
        public id?: number,
        public firstName?: string,
        public lastName?: string,
        public middleName?: string,
        public dateOfBirth?: any,
        public gender?: boolean,
    ) {
        this.gender = false;
    }
}
