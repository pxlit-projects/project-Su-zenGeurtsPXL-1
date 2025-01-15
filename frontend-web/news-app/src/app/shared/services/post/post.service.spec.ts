import {Post} from "../../models/posts/post.model";
import {Notification} from "../../models/posts/notification.model";
import {Filter} from "../../models/filter.model";

import {PostService} from "./post.service";

import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";
import {provideHttpClient} from "@angular/common/http";
import {Comment} from "../../models/comments/comment.model";
import {Review} from "../../models/reviews/review.model";

describe('PostService', () => {
  let service: PostService;
  let httpTestingController: HttpTestingController;

  let mockReviews: Review[] = [
    { id: 1, userId: 4, postId: 1, content: 'Content', createdAt: '2024-12-10 15:30:07', type: 'REJECTION'},
    { id: 2, userId: 2, postId: 1, content: 'Content', createdAt: '2024-12-11 15:30:07', type: 'APPROVAL'},
    { id: 3, userId: 3, postId: 1, content: 'Content', createdAt: '2024-12-15 15:30:07', type: 'COMMENT'}
  ];

  let mockComments: Comment[] = [
    { id: 1, userId: 4, postId: 1, content: 'Content', createdAt: '2024-12-10 15:30:07'},
    { id: 2, userId: 2, postId: 1, content: 'Content', createdAt: '2024-12-11 15:30:07'},
    { id: 3, userId: 3, postId: 1, content: 'Content', createdAt: '2024-12-15 15:30:07'}
  ];

  let mockPosts: Post[] = [
    { id: 1, title: 'Title', content: 'About a student', userId: 1, category: 'STUDENT', createdAt: '2024-12-10 15:30:07', state: 'DRAFTED'},
    { id: 2, title: 'Title', content: 'About sport', userId: 1, category: 'SPORTS', createdAt: '2024-12-5 15:30:07', state: 'SUBMITTED', reviews: mockReviews},
    { id: 3, title: 'Title', content: 'About alumni', userId: 1, category: 'ALUMNI', createdAt: '2024-12-7 15:30:07', state: 'PUBLISHED', comments: mockComments}
  ];

  let mockNotifications: Notification[] = [
    { id: 1, postId: 1, receiverId: 1, executorId: 3, content: 'Content', action: 'REJECTION', executedAt: '2024-12-7 15:30:07', isRead: false },
    { id: 2, postId: 1, receiverId: 1, executorId: 4, content: 'Content', action: 'APPROVAL', executedAt: '2024-12-10 15:30:07', isRead: false },
    { id: 3, postId: 1, receiverId: 1, executorId: 5, content: 'Content', action: 'COMMENT', executedAt: '2024-12-5 15:30:07', isRead: false }
  ];

  const mockCategories: string[] = [
    'STUDENT',
    'SPORTS',
    'ALUMNI'
  ]

  localStorage.setItem('userId', '5');
  localStorage.setItem('userRole', 'editor');

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PostService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(PostService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should retrieve posts by USERID via GET', () => {
    service.getPosts(true, false).subscribe(post => {
      expect(post).toEqual(mockPosts);
    });

    const req = httpTestingController.expectOne(service.api + '/mine');
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts);
  });

  it('should retrieve published posts via GET', () => {
    service.getPosts(false, false).subscribe(posts => {
      expect(posts).toEqual(mockPosts);
    });

    const req = httpTestingController.expectOne(service.api + '/published');
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts);
  });

  it('should retrieve reviewable posts via GET', () => {
    service.getPosts(false, true).subscribe(posts => {
      expect(posts).toEqual(mockPosts);
    });

    const req = httpTestingController.expectOne(service.api + '/reviewable');
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts);
  });

  it('should retrieve a single post by ID via GET', () => {
    const postId = 1;

    service.getPost(postId).subscribe(post => {
      expect(post).toEqual(mockPosts[0]);
    });

    const req = httpTestingController.expectOne(service.api + '/' + postId);
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts[0]);
  });

  it('should retrieve a single post with reviews by ID via GET', () => {
    const postId = 1;

    service.getPostWithReviews(postId).subscribe(post => {
      expect(post).toEqual(mockPosts[0]);
    });

    const req = httpTestingController.expectOne(service.api + '/' + postId + '/with-reviews');
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts[0]);
  });

  it('should retrieve a single post with comments by ID via GET', () => {
    const postId = 1;

    service.getPostWithComments(postId).subscribe(post => {
      expect(post).toEqual(mockPosts[0]);
    });

    const req = httpTestingController.expectOne(service.api + '/' + postId + '/with-comments');
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts[0]);
  });

  it('should retrieve categories via GET', () => {
    service.getCategories().subscribe(categories => {
      expect(categories).toEqual(mockCategories);
    });

    const req = httpTestingController.expectOne(service.api + '/category');
    expect(req.request.method).toBe('GET');
    req.flush(mockCategories);
  });

  it('should retrieve notifications by USERID via GET', () => {
    service.getMyNotifications().subscribe(notification => {
      expect(notification).toEqual(mockNotifications);
    });

    const req = httpTestingController.expectOne(service.api + '/notification/mine');
    expect(req.request.method).toBe('GET');
    req.flush(mockNotifications);
  });

  it('should add a post via POST', () => {
    const postRequest = {
      title: 'Title',
      content: 'Content',
      category: 'ACADEMIC',
    };

    service.addPost(postRequest).subscribe();


    const req = httpTestingController.expectOne(service.api);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(postRequest);
    req.flush(null);
  });

  it('should update content of a post via PUT', () => {
    const postId = 1;
    const content = 'Updated content';

    service.editPost(postId, content).subscribe();

    const req = httpTestingController.expectOne(service.api + '/' + postId);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(content);
    req.flush(null);
  });

  it('should update state of a post to SUBMITTED via PUT', () => {
    const postId = 1;

    service.submitPost(postId).subscribe();

    const req = httpTestingController.expectOne(service.api + '/' + postId + '/submit' );
    expect(req.request.method).toBe('PUT');
    req.flush(null);
  });

  it('should update state of a post to PUBLISHED via PUT', () => {
    const postId = 1;

    service.publishPost(postId).subscribe();

    const req = httpTestingController.expectOne(service.api + '/' + postId + '/publish' );
    expect(req.request.method).toBe('PUT');
    req.flush(null);
  });

  it('should update isRead of a notification to true via PUT', () => {
    const notificationId = 1;

    service.markNotificationAsRead(notificationId).subscribe();

    const req = httpTestingController.expectOne(service.api + '/notification/' + notificationId + '/read' );
    expect(req.request.method).toBe('PUT');
    req.flush(null);
  });

  it('should filter published posts based on the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', date: '2024-12-5' };

    service.filterPosts(filter, false, false).subscribe(posts => {
      expect(posts).toEqual([mockPosts[1]]);
    });

    const req = httpTestingController.expectOne(service.api + '/published');
    req.flush(mockPosts);
  });

  it('should filter posts by USERID based on the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', date: '2024-12-5' };

    service.filterPosts(filter, true, false).subscribe(posts => {
      expect(posts).toEqual([mockPosts[1]]);
    });

    const req = httpTestingController.expectOne(service.api + '/mine');
    req.flush(mockPosts);
  });

  it('should filter reviewable posts based on the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', date: '2024-12-5' };

    service.filterPosts(filter, false, true).subscribe(posts => {
      expect(posts).toEqual([mockPosts[1]]);
    });

    const req = httpTestingController.expectOne(service.api + '/reviewable');
    req.flush(mockPosts);
  });

  it('should match post correctly with the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', date: '2024-12-5' };
    expect(service.isPostMatchingFilter(mockPosts[0], filter)).toBeFalse();
    expect(service.isPostMatchingFilter(mockPosts[1], filter)).toBeTrue();
    expect(service.isPostMatchingFilter(mockPosts[2], filter)).toBeFalse();
  });

  it('should match post correctly with the filter criteria when no date is given', () => {
    const filter: Filter = { content: 'About sport', author: 'Milan', date: '' };
    expect(service.isPostMatchingFilter(mockPosts[0], filter)).toBeFalse();
    expect(service.isPostMatchingFilter(mockPosts[1], filter)).toBeTrue();
    expect(service.isPostMatchingFilter(mockPosts[2], filter)).toBeFalse();
  });
});
