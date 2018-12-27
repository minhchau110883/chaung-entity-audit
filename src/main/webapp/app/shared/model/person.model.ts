import { Moment } from 'moment';

export interface IPerson {
    id?: number;
    firstName?: string;
    lastName?: string;
    middleName?: string;
    dateOfBirth?: Moment;
    gender?: boolean;
}

export class Person implements IPerson {
    constructor(
        public id?: number,
        public firstName?: string,
        public lastName?: string,
        public middleName?: string,
        public dateOfBirth?: Moment,
        public gender?: boolean
    ) {
        this.gender = this.gender || false;
    }
}
