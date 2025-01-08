import {Injectable} from '@angular/core';
import {User} from "../../models/authentication/user.model";
import {LoginRequest} from "../../models/authentication/login-request.model";

@Injectable({
  providedIn: 'root'
})

export class AuthenticationService {
  private users: User[] = [
    { id: 1, username: 'milan', fullName: 'Milan Dewaele', password: 'ed123', role: 'editor', email: 'janedoepxl@gmail.com'},
    { id: 2, username: 'lara', fullName: 'Lara Peeters', password: 'ed123', role: 'editor', email: 'janedoepxl@gmail.com' },
    { id: 3, username: 'johan', fullName: 'Johan Willems', password: 'ed123', role: 'editor', email: 'janedoepxl@gmail.com'},
    { id: 4, username: 'emma', fullName: 'Emma Janssen', password: 'ed123', role: 'editor', email: 'janedoepxl@gmail.com' },
    { id: 5, username: 'noah', fullName: 'Noah Verhoeven', password: 'ed123', role: 'editor', email: 'janedoepxl@gmail.com' },
    { id: 6, username: 'lukas', fullName: 'Lukas Meijer', password: 'ed123', role: 'editor', email: 'janedoepxl@gmail.com' },
    { id: 7, username: 'sofie', fullName: 'Sofie De Vries', password: 'ed123', role: 'editor', email: 'janedoepxl@gmail.com' },
    { id: 8, username: 'tom', fullName: 'Tom Jacobs', password: 'us123', role: 'user', email: 'janedoepxl@gmail.com' },
    { id: 9, username: 'anouk', fullName: 'Anouk Claes', password: 'us123', role: 'user', email: 'janedoepxl@gmail.com' },
    { id: 10, username: 'kasper', fullName: 'Kasper De Smet', password: 'us123', role: 'user', email: 'janedoepxl@gmail.com' }
  ];

  login(login: LoginRequest): User | null {
    const username = login.username;
    const password = login.password;

    const user = this.users.find(user => user.username === username);

    if (user !== undefined && user.password == password) {
        return user;
    } else {
      return null;
    }
  }

  getUserById(id: number | string | null): User | undefined {
    if (typeof id === 'string') {
      id = Number(id);
    }
    return this.users.find(user => user.id === id);
  }

  getHeaders(): any {
    const userId = localStorage.getItem('userId') == null ? '' : localStorage.getItem('userId');
    const userRole = localStorage.getItem('userId') == null ? '' : localStorage.getItem('userRole');
    const email = localStorage.getItem('userId') == null ? '' : this.getUserById(userId)?.email;
    return { userId: userId, userRole: userRole, email: email, 'Content-Type': 'application/json' };
  }
}
