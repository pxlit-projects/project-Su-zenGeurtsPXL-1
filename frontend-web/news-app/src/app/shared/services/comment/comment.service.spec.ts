import {CommentService} from "./comment.service";

import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";

describe('CommentService', () => {
  let service : CommentService;
  let httpTestingController: HttpTestingController;

  localStorage.setItem('userId', '5');
  localStorage.setItem('userRole', 'editor');

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CommentService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(CommentService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should add a comment via POST', () => {
    const commentRequest = {
      postId: 1,
      content: 'Content',
    };

    service.addComment(commentRequest).subscribe();

    const req = httpTestingController.expectOne(service.api);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(commentRequest);
    req.flush(null);
  });

  it('should update content of a comment via PUT', () => {
    const commentId = 1;
    const content = 'Updated content';

    service.editComment(commentId, content).subscribe();

    const req = httpTestingController.expectOne(service.api + '/' + commentId);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(content);
    req.flush(null);
  });

  it('should delete a comment via DELETE', () => {
    const commentId = 1;

    service.deleteComment(commentId).subscribe();

    const req = httpTestingController.expectOne(service.api + '/' + commentId );
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
