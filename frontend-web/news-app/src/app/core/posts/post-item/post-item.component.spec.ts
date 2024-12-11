import {Post} from "../../../shared/models/post.model";
import {PostService} from "../../../shared/services/post.service";
import {PostItemComponent} from "./post-item.component";

import {ComponentFixture, TestBed} from "@angular/core/testing";
import {provideRouter} from "@angular/router";
import {NO_ERRORS_SCHEMA} from "@angular/core";
import {By} from "@angular/platform-browser";
import { provideHttpClient, provideHttpClientTesting, HttpTestingController } from "@angular/common/http/testing";

describe('PostItemComponent', () => {
  let component: PostItemComponent;
  let fixture: ComponentFixture<PostItemComponent>;
  let httpTestingController: HttpTestingController;

  const mockPost: Post = new Post(1, 'Title1', 'Content...', 1, 'ACADEMIC', '2024-12-10 15:30:07', 'PUBLISHED');

  beforeEach(() => {
    // postServiceMock = jasmine.createSpyObj('PostService', ['toPascalCasing', 'transformDate']);
    TestBed.configureTestingModule({
      imports: [PostItemComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
      schemas: [NO_ERRORS_SCHEMA]
    });

    fixture = TestBed.createComponent(PostItemComponent);
    component = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify(); // Ensure no outstanding HTTP requests
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should render post title in the template', () => {
    component.post = mockPost;
    fixture.detectChanges();

    const debugElement = fixture.debugElement.query(By.css('h1'));
    expect(debugElement.nativeElement.textContent).toContain('Title1');
  });

});
