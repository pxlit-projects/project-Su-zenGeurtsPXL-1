export interface Post {
  id: number;
  title: string;
  content: string;
  userId: number;
  category: string;
  createdAt: Date;
  state: string;
}
