export class Post {
  id?: number;
  title: string;
  content: string;
  userId: number;
  category: string;
  createdAt: string;
  state: string;

  constructor(title: string, content: string, userId: number, category: string, createdAt: string, state: string) {
    this.title = title;
    this.content = content;
    this.userId = userId;
    this.category = category;
    this.createdAt = createdAt;
    this.state = state;
  }
}
