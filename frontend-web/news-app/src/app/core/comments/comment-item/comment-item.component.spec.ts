import {ComponentFixture, TestBed} from "@angular/core/testing";
import {CommentItemComponent} from "./comment-item.component";
import {CommentService} from "../../../shared/services/comment/comment.service";
import {HelperService} from "../../../shared/services/helper/helper.service";
import {of} from "rxjs";

describe('CommentItemComponent', () => {
  let component: CommentItemComponent;
  let fixture: ComponentFixture<CommentItemComponent>;
  let commentServiceMock: jasmine.SpyObj<CommentService>;
  let helperServiceMock: jasmine.SpyObj<HelperService>;

  const mockComment = {id: 1, userId: 4, postId: 1, content: 'Content', createdAt: '2024-12-10 15:30:07'};

  beforeEach(async () => {
    commentServiceMock = jasmine.createSpyObj('CommentService', ['editComment', 'deleteComment']);
    helperServiceMock = jasmine.createSpyObj('HelperService', ['transformDateShort']);

    await TestBed.configureTestingModule({
      imports: [CommentItemComponent],
      providers: [
        { provide: CommentService, useValue: commentServiceMock },
        { provide: HelperService, useValue: helperServiceMock },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CommentItemComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  xit('should delete comment on deleteComment', () => {
    component.menuIsHidden = false;
    component.comment = mockComment;

    spyOn(window, 'confirm').and.returnValue(true);
    fixture.detectChanges();

    component.deleteComment();

    expect(commentServiceMock.deleteComment).toHaveBeenCalled();
    expect(component.menuIsHidden).toBeTrue();
  });

  xit('should activate update mode on updateComment', () => {
    component.menuIsHidden = false;
    component.isInProgress = false;
    component.comment = mockComment;

    const form = {
      content: ''
    };

    component.commentForm.setValue(form);

    component.updateComment();
    fixture.detectChanges();

    expect(component.commentForm.value.content).toEqual('Content');
    expect(component.isInProgress).toBeTrue();
    expect(component.menuIsHidden).toBeTrue();
  });

  it('should call updateComment on form submit on success', () => {
    const content = 'Updated content';

    const form = {
      content: content
    };

    component.commentForm.setValue(form);

    component.comment = mockComment;
    fixture.detectChanges();

    commentServiceMock.editComment.and.returnValue(of(undefined));

    component.onSubmit();

    expect(commentServiceMock.editComment).toHaveBeenCalledWith(1, content);

    component.comment.content = content;
  });

  it('should cancel correctly', () => {
    component.isInProgress = true;
    component.menuIsHidden = true;

    component.cancel();

    expect(component.isInProgress).toBeFalse();
    expect(component.menuIsHidden).toBeFalse();
  });

  it('should open menu correctly', () => {
    component.menuIsHidden = true;
    component.isInProgress = false;
    localStorage.setItem('userId', '4');

    component.comment = mockComment;
    fixture.detectChanges();

    component.openMenu();

    expect(component.menuIsHidden).toBeFalse();
  });
});
