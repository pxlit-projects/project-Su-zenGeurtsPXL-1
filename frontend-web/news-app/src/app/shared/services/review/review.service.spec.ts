import {ReviewService} from "./review.service";

import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";

describe('ReviewService', () => {
  let service : ReviewService;
  let httpTestingController: HttpTestingController;

  localStorage.setItem('userId', '5');
  localStorage.setItem('userRole', 'editor');

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ReviewService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(ReviewService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should review a post via POST', () => {
    const postId = 1;
    const reviewType = 'approve';

    const reviewRequest = {
      postId: postId,
      content: 'Could be better',
      category: 'ACADEMIC',
    };

    service.reviewPost(reviewType, reviewRequest).subscribe();

    const req = httpTestingController.expectOne(service.api + '/' + reviewType);
    expect(req.request.body).toEqual(reviewRequest);
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });
});
