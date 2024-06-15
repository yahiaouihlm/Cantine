import { TestBed } from '@angular/core/testing';

import { CoreCantineService } from './core-cantine.service';

describe('CoreCantineServiceService', () => {
  let service: CoreCantineService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CoreCantineService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
