export class Post {
  id: number;
  title: string;
  content: string;
  userId: number;
  category: string;
  createdAt: Date;
  state: string;

  constructor(id: number, title: string, content: string, userId: number, category: string, createdAt: Date, state: string) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.userId = userId;
    this.category = category;
    this.createdAt = createdAt;
    this.state = state;
  }
}
