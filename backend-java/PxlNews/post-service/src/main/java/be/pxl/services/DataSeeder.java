package be.pxl.services;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.Notification;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.State;
import be.pxl.services.repository.NotificationRepository;
import be.pxl.services.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    public DataSeeder(PostRepository postRepository, NotificationRepository notificationRepository) {
        this.postRepository = postRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void run(String... args) {
        if (postRepository.count() == 0) {
            logger.info("Seeding posts");
            Post post1 = Post.builder()
                    .title("1,000 Limburg Students Compete in LEGO League at PXL University of Applied Sciences")
                    .content("The PXL University of Applied Sciences campus in Hasselt was fully dedicated to technology and innovation on Thursday and Friday. A total of 1,000 enthusiastic students from 76 schools across Limburg gathered here to participate in the twelfth edition of the regional final of the FIRST LEGO League, a prestigious international competition." +
                    "\n\nThis international event aims to promote technology and innovation among children aged 9 to 14. The children learn to engage with technology by programming robots and having them carry out specific missions. Leela Schoyen (11) and Valerie Veracht (10) from Go! Freinetschool De Step in Beringen also attended. Apart from a few penalties, things went really well,” the two laugh.")
                    .userId(4L)
                    .category(Category.CAMPUS)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 8, 30, 0))
                    .state(State.APPROVED)
                    .build();

            postRepository.save(post1);

            Post post2 = Post.builder()
                    .title("PXL Students Shine at Hack The Future Hackathon")
                    .content("PXL students showcased their best talents during the Hack The Future hackathon, the largest technology and design competition in Belgium, organized by technology company Cronos. Gijs Rommers and Kiara Petillon, Applied Informatics students, won the main prize in one of the challenges." +
                            "\n\nGijs and Kiara created a web application called 'Galactic Explorer,' allowing users to explore a galaxy. With Laravel and Vue.js, they developed a user-friendly and innovative platform. The jury praised them for their strong technical execution and creative approach." +
                            "\n\nStudents from the Digital Design program also participated in the hackathon. They worked on design tasks related to the theme of ‘space discovery,’ such as creating branding for a fictional planet or designing a marketing campaign for a space travel company.")
                    .userId(1L)
                    .category(Category.ACADEMIC)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 3, 9, 0, 0))
                    .state(State.PUBLISHED)
                    .build();

            postRepository.save(post2);

            Post post3 = Post.builder()
                    .title("Limburg AI Team, Including a Teacher from Syntra-PXL, Creates Special Effects for Hollywood Film 'Here' Starring Tom Hanks and Robin Wright")
                    .content("Pioneer and specialist in artificial intelligence Chris Umé from Beringen went to see the film ‘Here’ in Kinepolis Hasselt on Sunday with family, friends and his Belgian team from Metaphysic. In the film, lead actors Tom Hanks and Robin Wright were made both (much) younger and older by Metaphysic with artificial intelligence. None of the team, except Chris who was at the American premiere, had seen the film before." +
                            "The team also included ten Belgians, eight of whom were from Limburg: “There was also a small, close-knit group of Limburgers, including several students and a teacher from Syntra-PXL,” says Umé. “When we are looking for people, enthusiasm and the will to work are more important than studies or a diploma.”")
                    .userId(4L)
                    .category(Category.STUDENT)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 5, 15, 48, 0))
                    .state(State.SUBMITTED)
                    .build();

            postRepository.save(post3);

            Post post4 = Post.builder()
                    .title("PXL Rowing Team’s Nude Calendar Raises €1,000 for Kom op tegen Kanker")
                    .content("The 'Nude Calendar 2024' was a special milestone for the PXL rowing team, who compiled this calendar for the seventh time. The photos were taken at PXL-NeXT’s conference building in Hasselt by photographer Toon Van Den Broek, and printing was done by Haletra. The PXL rowing team chose to donate the proceeds from the calendar sales to Kom Op Tegen Kanker. Inspired by their sports coordinator Erik Vanmierlo, this choice highlighted the team’s commitment." +
                            "\n\nThe calendars were sold for €10 each at the Stuvo counter at PXL University of Applied Sciences and were quickly sold out. “With this nude calendar, we wanted not only to celebrate the beauty of sport but also to contribute to the fight against cancer,” said captain Lukas Gendera. “We are grateful for the support from our PXL community and hope that the proceeds from our calendar sales will contribute to cancer research.”" +
                            "\n\n“Unfortunately, cancer is also present in the world of sports,” said Tim Van Werde, district coordinator Limburg for Kom op tegen Kanker. “It’s encouraging when active young people contribute to helping eliminate cancer. Most people know that exercise is good for preventing heart disease and diabetes. But it also reduces the risk of cancer.”")
                    .userId(4L)
                    .category(Category.SPORTS)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 14, 16, 4, 0))
                    .state(State.SUBMITTED)
                    .build();

            postRepository.save(post4);

            Post post5 = Post.builder()
                    .title("Expo The Research Group")
                    .content("The artist collective featuring Hugo Duchateau, Jos Jans, Hélène Keil, Dré Sprakenis, Vincent Van Den Meersch, and Jan Withofs, active from 1967 to 1972, created a turning point in contemporary art in Limburg. Through striking and playful performances and group exhibitions," +
                            "they made significant strides compared to the international art scene. Fifty years later, their impact is still felt, and their works remain relevant. Time for a retrospective!" +
                            "\n\nOpening of the exhibition on Friday, 20 September 2024, at 19:00 at Het Stadsmus, Hasselt. Welcome! Please register your attendance here before 15 September 2024." +
                            "\n\nOn the evening of the opening, you will have the opportunity to purchase the accompanying publication, authored by Edith Doove and designed by Geoffrey Brussatto." +
                            "\n\nThe exhibition and publication were made possible in collaboration with School of Arts PXL-MAD, with support from the Flemish Community.")
                    .userId(4L)
                    .category(Category.RESEARCH)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 10, 10, 32, 0))
                    .state(State.PUBLISHED)
                    .build();

            postRepository.save(post5);

            Post post6 = Post.builder()
                    .title("PXL-MAD Alumni Nominated for Global Creative Graduate Showcase 2024")
                    .content("Tyana Verstraete and Jiawei Xu, alumni of the School of Arts PXL-MAD, have been nominated for the Global Creative Graduate Showcase 2024 in collaboration with WGSN, Coloro, and Google Arts & Culture. The public vote is officially open until Friday, 11 October. The winners will be announced on Monday, 14 October.")
                    .userId(3L)
                    .category(Category.ALUMNI)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 8, 14, 56, 0))
                    .state(State.REJECTED)
                    .build();

            postRepository.save(post6);

            Post post7 = Post.builder()
                    .title("PXL takes in 75 children during strike day: “In no time all places were taken”")
                    .content("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." +
                            "\n\nSed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?")
                    .userId(3L)
                    .category(Category.ALUMNI)
                    .createdAt(LocalDateTime.of(2025, Month.JANUARY, 15, 15, 0, 0))
                    .state(State.PUBLISHED)
                    .build();

            Post post8 = Post.builder()
                    .title("Ann Bessemans and Kevin Bormans selected for HOWdesign in Mexico")
                    .content("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." +
                            "\n\nSed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?")
                    .userId(5L)
                    .category(Category.SPORTS)
                    .createdAt(LocalDateTime.of(2025, Month.JANUARY, 15, 9, 30, 0))
                    .state(State.PUBLISHED)
                    .build();

            Post post9 = Post.builder()
                    .title("Behind the blindes")
                    .content("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." +
                            "\n\nSed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?")
                    .userId(6L)
                    .category(Category.ACADEMIC)
                    .createdAt(LocalDateTime.of(2025, Month.JANUARY, 13, 13, 11, 0))
                    .state(State.PUBLISHED)
                    .build();

            Post post10 = Post.builder()
                    .title("Zion Luts is one of the new heroes from '#LikeMe': \"What happened to the previous generation, we can only dream of\"")
                    .content("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." +
                            "\n\nSed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?")
                    .userId(1L)
                    .category(Category.CAMPUS)
                    .createdAt(LocalDateTime.of(2025, Month.JANUARY, 11, 16, 42, 0))
                    .state(State.PUBLISHED)
                    .build();

            postRepository.save(post7);
            postRepository.save(post8);
            postRepository.save(post9);
            postRepository.save(post10);
        }

        if (notificationRepository.count() == 0) {
            logger.info("Seeding notifications");
            Notification notification1 = Notification.builder()
                    .postId(1L)
                    .receiverId(4L)
                    .executorId(1L)
                    .content("Focus on the results.")
                    .action("REJECTION")
                    .executedAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 9, 0, 0))
                    .isRead(true)
                    .build();

            notificationRepository.save(notification1);

            Notification notification2 = Notification.builder()
                    .postId(1L)
                    .receiverId(4L)
                    .executorId(5L)
                    .content("And implement more details.")
                    .action("COMMENT")
                    .executedAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 9, 5, 0))
                    .isRead(true)
                    .build();

            notificationRepository.save(notification2);

            Notification notification3 = Notification.builder()
                    .postId(1L)
                    .receiverId(4L)
                    .executorId(5L)
                    .content("Great!")
                    .action("APPROVAL")
                    .executedAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 15, 35, 0))
                    .isRead(false)
                    .build();

            notificationRepository.save(notification3);

            Notification notification4 = Notification.builder()
                    .postId(1L)
                    .receiverId(4L)
                    .executorId(7L)
                    .content("I also love it!")
                    .action("COMMENT")
                    .executedAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 15, 53, 0))
                    .isRead(false)
                    .build();

            notificationRepository.save(notification4);

            Notification notification5 = Notification.builder()
                    .postId(1L)
                    .receiverId(4L)
                    .executorId(3L)
                    .content("Well done!")
                    .action("COMMENT")
                    .executedAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 15, 58, 0))
                    .isRead(false)
                    .build();

            notificationRepository.save(notification5);

            Notification notification6 = Notification.builder()
                    .postId(3L)
                    .receiverId(4L)
                    .executorId(6L)
                    .content("This is way too short!")
                    .action("REJECTION")
                    .executedAt(LocalDateTime.of(2024, Month.DECEMBER, 5, 16, 28, 0))
                    .isRead(false)
                    .build();

            notificationRepository.save(notification6);

            Notification notification7 = Notification.builder()
                    .postId(4L)
                    .receiverId(4L)
                    .executorId(1L)
                    .content("Also focus on the reason why")
                    .action("REJECTION")
                    .executedAt(LocalDateTime.of(2024, Month.DECEMBER, 14, 17, 13, 0))
                    .isRead(false)
                    .build();

            notificationRepository.save(notification7);

            Notification notification8 = Notification.builder()
                    .postId(6L)
                    .receiverId(3L)
                    .executorId(7L)
                    .content("This is way too short!")
                    .action("REJECTION")
                    .executedAt(LocalDateTime.of(2024, Month.DECEMBER, 8, 15, 42, 0))
                    .isRead(false)
                    .build();

            notificationRepository.save(notification8);
        }
    }
}