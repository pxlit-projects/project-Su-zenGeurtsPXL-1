import {Post} from "../../models/posts/post.model";
import {PostService} from "./post.service";
import {Filter} from "../../models/filter.model";

import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";
import {provideHttpClient} from "@angular/common/http";
import {of} from "rxjs";


describe('PostService', () => {
  let service: PostService;
  let httpTestingController: HttpTestingController;

  let mockPosts: Post[] = [];

  const mockCategories: string[] = [
    'STUDENT',
    'SPORTS',
    'ALUMNI'
  ]

  beforeEach(() => {
    localStorage.setItem('userId', '5');
    localStorage.setItem('userRole', 'editor');

    mockPosts = [
      { id: 1, title: 'Title', content: 'About a student', userId: 1, category: 'STUDENT', createdAt: '2024-12-10 15:30:07', state: 'DRAFTED'},
      { id: 2, title: 'Title', content: 'About sport', userId: 1, category: 'SPORTS', createdAt: '2024-12-5 15:30:07', state: 'SUBMITTED'},
      { id: 3, title: 'Title', content: 'About alumni', userId: 1, category: 'ALUMNI', createdAt: '2024-12-7 15:30:07', state: 'PUBLISHED'}
    ];

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

  // getPost()
  it('should retrieve a single post by ID via GET', () => {
    const postId = 1;

    service.getPost(postId).subscribe(post => {
      expect(post).toEqual(mockPosts[0]);
    });

    const req = httpTestingController.expectOne(service.postApi + '/' + postId);
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts[0]);
  });


  // getCategories()
  it('should retrieve categories via GET', () => {
    service.getCategories().subscribe(categories => {
      expect(categories).toEqual(mockCategories);
    });

    const req = httpTestingController.expectOne(service.postApi + '/category');
    expect(req.request.method).toBe('GET');
    req.flush(mockCategories);
  });

  // getPublishedPosts()
  it('should retrieve published posts via GET', () => {
    service.getPublishedPosts().subscribe(posts => {
      expect(posts).toEqual(mockPosts);
    });

    const req = httpTestingController.expectOne(service.postApi + '/published');
    expect(req.request.method).toBe('GET');
    req.flush(mockPosts);
  });

  // getMyPosts()
  it('should retrieve posts by USERID via GET', () => {

    service.getMyPosts().subscribe(post => {
      expect(post).toEqual(mockPosts);
    });

    const req = httpTestingController.expectOne(service.postApi + '/mine');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Content-Type')).toEqual('application/json');
    expect(req.request.headers.get('userId')).toEqual('5');
    expect(req.request.headers.get('userRole')).toEqual('editor');
    req.flush(mockPosts);
  });

  // getReviewablePosts()
  it('should retrieve reviewable posts via GET', () => {
    service.getReviewablePosts().subscribe(posts => {
      expect(posts).toEqual(mockPosts);
    });

    const req = httpTestingController.expectOne(service.postApi + '/reviewable');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Content-Type')).toEqual('application/json');
    expect(req.request.headers.get('userId')).toEqual('5');
    expect(req.request.headers.get('userRole')).toEqual('editor');
    req.flush(mockPosts);
  });

  // getPostWithReviews()
  it('should retrieve a single post with reviews by ID via GET', () => {
    const postId = 1;

    service.getPostWithReviews(postId).subscribe(post => {
      expect(post).toEqual(mockPosts[0]);
    });

    const req = httpTestingController.expectOne(service.postApi + '/' + postId + '/with-reviews');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Content-Type')).toEqual('application/json');
    expect(req.request.headers.get('userRole')).toEqual('editor');
    req.flush(mockPosts[0]);
  });

  // addPost()
  it('should add a post via POST', () => {
    const postRequest = {
      title: 'Title',
      content: 'Content...',
      category: 'ACADEMIC',
    };

    const newPost = {
      id: 4,
      title: 'Title',
      content: 'Content...',
      userId: 1,
      category: 'ACADEMIC',
      createdAt: '2024-12-18 15:30:07',
      state: 'DRAFTED'
    };

    service.addPost(postRequest).subscribe(post => {
      expect(post).toEqual(newPost);
    });

    const req = httpTestingController.expectOne(service.postApi);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(postRequest);
    expect(req.request.headers.get('Content-Type')).toEqual('application/json');
    expect(req.request.headers.get('userId')).toEqual('5');
    expect(req.request.headers.get('userRole')).toEqual('editor');
    req.flush(newPost);
  });

  // submitPost()
  it('should submit a post via POST', () => {
    const postId = 1;

    service.submitPost(postId).subscribe();

    const req = httpTestingController.expectOne(service.postApi + '/' + postId + '/submit' );
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Content-Type')).toEqual('application/json');
    expect(req.request.headers.get('userId')).toEqual('5');
    expect(req.request.headers.get('userRole')).toEqual('editor');
    req.flush(null);
  });

  // publishPost()
  it('should publish a post via POST', () => {
    const postId = 1;

    service.publishPost(postId).subscribe();

    const req = httpTestingController.expectOne(service.postApi + '/' + postId + '/publish' );
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Content-Type')).toEqual('application/json');
    expect(req.request.headers.get('userId')).toEqual('5');
    expect(req.request.headers.get('userRole')).toEqual('editor');
    req.flush(null);
  });

  // reviewPost()
  it('should review a post via POST', () => {
    const postId = 1;
    const reviewType = 'approve';

    const reviewRequest = {
      postId: postId,
      content: 'Could be better',
      category: 'ACADEMIC',
    };

    service.reviewPost(reviewType, reviewRequest).subscribe();

    const req = httpTestingController.expectOne(service.reviewApi + '/' + reviewType);
    expect(req.request.body).toEqual(reviewRequest);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Content-Type')).toEqual('application/json');
    expect(req.request.headers.get('userId')).toEqual('5');
    expect(req.request.headers.get('userRole')).toEqual('editor');
    req.flush(null);
  });

  // editPost()
  it('should update a post via PUT', () => {
    const postId = 1;

    const updatedPost = { ...mockPosts[0], content: 'Updated content' };

    service.editPost(postId, updatedPost).subscribe(post => {
      expect(post).toEqual(updatedPost);
    });

    const req = httpTestingController.expectOne(service.postApi + '/' + postId);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedPost);
    expect(req.request.headers.get('Content-Type')).toEqual('application/json');
    expect(req.request.headers.get('userId')).toEqual('5');
    expect(req.request.headers.get('userRole')).toEqual('editor');
    req.flush(updatedPost);
  });

  // filterPublishedPosts()
  it('should filter published posts based on the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', date: '2024-12-5' };

    service.filterPublishedPosts(filter).subscribe(posts => {
      expect(posts).toEqual([mockPosts[1]]);
    });

    const req = httpTestingController.expectOne(service.postApi + '/published');
    req.flush(mockPosts);
  });

  // filterMyPosts()
  it('should filter posts by USERID based on the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', date: '2024-12-5' };

    service.filterMyPosts(filter).subscribe(posts => {
      expect(posts).toEqual([mockPosts[1]]);
    });

    const req = httpTestingController.expectOne(service.postApi + '/mine');
    req.flush(mockPosts);
  });

  // filterReviewablePosts()
  it('should filter reviewable posts based on the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', date: '2024-12-5' };

    service.filterReviewablePosts(filter).subscribe(posts => {
      expect(posts).toEqual([mockPosts[1]]);
    });

    const req = httpTestingController.expectOne(service.postApi + '/reviewable');
    req.flush(mockPosts);
  });

  // isPostMatchingFilter()
  it('should match post correctly with the filter criteria', () => {
    const filter: Filter = { content: 'About', author: 'Milan', date: '2024-12-5' };
    expect(service.isPostMatchingFilter(mockPosts[0], filter)).toBeFalse();
    expect(service.isPostMatchingFilter(mockPosts[1], filter)).toBeTrue();
    expect(service.isPostMatchingFilter(mockPosts[2], filter)).toBeFalse();
  });

  // isPostMatchingFilter() without date
  it('should match post correctly with the filter criteria when no date is given', () => {
    const filter: Filter = { content: 'About sport', author: 'Milan', date: '' };
    expect(service.isPostMatchingFilter(mockPosts[0], filter)).toBeFalse();
    expect(service.isPostMatchingFilter(mockPosts[1], filter)).toBeTrue();
    expect(service.isPostMatchingFilter(mockPosts[2], filter)).toBeFalse();
  });

  it('should not filter when filter criteria of user is broken', () => {
    mockPosts.push({ id: 4, title: 'Title', content: 'Content...', userId: 9999, category: 'ACADEMIC', createdAt: '2024-12-10 15:30:07', state: 'PUBLISHED'});
    const filter: Filter = { content: 'About', author: 'Milan', date: '2024-12-5' };

    expect(service.isPostMatchingFilter(mockPosts[mockPosts.length - 1], filter)).toBeFalse();
  })

  // transformDate()
  it('should format date strings to certain format correctly', () => {
    const word = '2024-12-10 15:30:07'
    const expectedWord = 'Tuesday, December 10, 2024 at 3:30 PM'
    expect(service.transformDate(word)).toBe(expectedWord);
  })

  // transformDateShort()
  it('should format date strings to certain short format correctly', () => {
    const word = '2024-12-10 15:30:07'
    const expectedWord = '10/12/2024 15:30'
    expect(service.transformDateShort(word)).toBe(expectedWord);
  })

  // toPascalCasing()
  it('should format strings to PascalCasing correctly', () => {
    const word = 'ACADEMIC'
    const expectedWord = 'Academic'
    expect(service.toPascalCasing(word)).toBe(expectedWord);
  })

  // orderToMostRecent()
  it('should order the array correctly', () => {
    const expectedPosts: Post[] = [
      { id: 1, title: 'Title', content: 'About a student', userId: 1, category: 'STUDENT', createdAt: '2024-12-10 15:30:07', state: 'DRAFTED'},
      { id: 3, title: 'Title', content: 'About alumni', userId: 1, category: 'ALUMNI', createdAt: '2024-12-7 15:30:07', state: 'PUBLISHED'},
      { id: 2, title: 'Title', content: 'About sport', userId: 1, category: 'SPORTS', createdAt: '2024-12-5 15:30:07', state: 'SUBMITTED'}
    ];

    service.orderToMostRecent(of(mockPosts)).subscribe(posts => {
      expect(posts).toEqual(expectedPosts);
    });
  });

  // getHeaders()
  it('should return headers correctly', () => {
    localStorage.clear();
    expect(service.getHeaders()).toEqual({ userId: '', userRole: '', 'Content-Type': 'application/json' });

    localStorage.setItem('userId', '1');
    localStorage.setItem('userRole', 'editor');
    expect(service.getHeaders()).toEqual({ userId: '1', userRole: 'editor', 'Content-Type': 'application/json' });
  });
});
