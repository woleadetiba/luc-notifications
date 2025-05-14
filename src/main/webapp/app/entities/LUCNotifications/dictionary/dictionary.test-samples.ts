import { IDictionary, NewDictionary } from './dictionary.model';

export const sampleWithRequiredData: IDictionary = {
  id: 836,
};

export const sampleWithPartialData: IDictionary = {
  id: 15146,
  keyName: 'spotless hence pish',
  description: 'velocity oof dirty',
};

export const sampleWithFullData: IDictionary = {
  id: 29006,
  keyName: 'gosh',
  keyCode: 'disgorge describe',
  label: 'unaccountably',
  description: 'unless once',
};

export const sampleWithNewData: NewDictionary = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
