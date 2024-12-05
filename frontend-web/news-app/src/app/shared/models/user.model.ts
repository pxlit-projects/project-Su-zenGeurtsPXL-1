export class User {
  id: number;
  username: string;
  email: string;
  password: number;

  constructor(id: number, username: string, email: string, password: number) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
  }
}
