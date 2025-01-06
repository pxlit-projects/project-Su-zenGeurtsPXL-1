import {HelperService} from "./helper.service";
import {TestBed} from "@angular/core/testing";
describe('HelperService', () => {
  let service: HelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ HelperService ]
    });

    service = TestBed.inject(HelperService);
    });

  it('should format date strings to certain format correctly', () => {
    const word = '2024-12-10 15:30:07'
    const expectedWord = 'Tuesday, December 10, 2024 at 3:30 PM'
    expect(service.transformDate(word)).toBe(expectedWord);
  });

  it('should format date strings to certain short format correctly', () => {
    const word = '2024-12-10 15:30:07'
    const expectedWord = '10/12/2024 15:30'
    expect(service.transformDateShort(word)).toBe(expectedWord);
  })

  it('should format strings to PascalCasing correctly', () => {
    const word = 'ACADEMIC'
    const expectedWord = 'Academic'
    expect(service.toPascalCasing(word)).toBe(expectedWord);
  })
});
