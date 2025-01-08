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
                    .content("""
                            The PXL University of Applied Sciences campus in Hasselt was fully dedicated to technology and innovation on Thursday and Friday.
                            A total of 1,000 enthusiastic students from 76 schools across Limburg gathered here to participate in the twelfth edition of the regional final of the FIRST LEGO League, a prestigious international competition.
                            This international event aims to promote technology and innovation among children aged 9 to 14.
                            The children learn to engage with technology by programming robots and having them carry out specific missions.
                            Leela Schoyen (11) and Valerie Veracht (10) from Go! Freinetschool De Step in Beringen also attended.
                            “Apart from a few penalties, things went really well,” the two laugh.
                            """)
                    .userId(4L)
                    .category(Category.CAMPUS)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 8, 30, 0))
                    .state(State.APPROVED)
                    .build();

            postRepository.save(post1);

            Post post2 = Post.builder()
                    .title("PXL Students Shine at Hack The Future Hackathon")
                    .content("""
                            PXL students showcased their best talents during the Hack The Future hackathon, the largest technology and design competition in Belgium, organized by technology company Cronos.
                            Gijs Rommers and Kiara Petillon, Applied Informatics students, won the main prize in one of the challenges.
                            Gijs and Kiara created a web application called 'Galactic Explorer,' allowing users to explore a galaxy.
                            With Laravel and Vue.js, they developed a user-friendly and innovative platform.
                            The jury praised them for their strong technical execution and creative approach.
                            Students from the Digital Design program also participated in the hackathon.
                            They worked on design tasks related to the theme of ‘space discovery,’ such as creating branding for a fictional planet or designing a marketing campaign for a space travel company.
                            """)
                    .userId(1L)
                    .category(Category.ACADEMIC)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 3, 9, 0, 0))
                    .state(State.PUBLISHED)
                    .build();

            postRepository.save(post2);

            Post post3 = Post.builder()
                    .title("Limburg AI Team, Including a Teacher from Syntra-PXL, Creates Special Effects for Hollywood Film 'Here' Starring Tom Hanks and Robin Wright")
                    .content("""
                            Pioneer and artificial intelligence specialist Chris Umé from Beringen, along with family, friends, and the Belgian Metaphysic team, went to see the film 'Here' at Kinepolis Hasselt.
                            In the film, Metaphysic made main actors Tom Hanks and Robin Wright appear much younger and older using artificial intelligence.
                            None of the team members, except Chris who attended the American premiere, had seen the film until then.
                            The team included about ten Belgians, eight of whom were from Limburg: “We look for enthusiasm and a willingness to work, rather than just academic studies or diplomas,” says Umé.
                            """)
                    .userId(4L)
                    .category(Category.STUDENT)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 5, 15, 48, 0))
                    .state(State.SUBMITTED)
                    .build();

            postRepository.save(post3);

            Post post4 = Post.builder()
                    .title("PXL Rowing Team’s Nude Calendar Raises €1,000 for Kom op tegen Kanker")
                    .content("""
                            The 'Nude Calendar 2024' was a special milestone for the PXL rowing team, who compiled this calendar for the seventh time.
                            The photos were taken at PXL-NeXT’s conference building in Hasselt by photographer Toon Van Den Broek, and printing was done by Haletra.
                            The PXL rowing team chose to donate the proceeds from the calendar sales to Kom Op Tegen Kanker.
                            Inspired by their sports coordinator Erik Vanmierlo, this choice highlighted the team’s commitment.
                            The calendars were sold for €10 each at the Stuvo counter at PXL University of Applied Sciences and were quickly sold out.
                            “With this nude calendar, we wanted not only to celebrate the beauty of sport but also to contribute to the fight against cancer,” said captain Lukas Gendera.
                            “We are grateful for the support from our PXL community and hope that the proceeds from our calendar sales will contribute to cancer research.”
                            “Unfortunately, cancer is also present in the world of sports,” said Tim Van Werde, district coordinator Limburg for Kom op tegen Kanker.
                            “It’s encouraging when active young people contribute to helping eliminate cancer. Most people know that exercise is good for preventing heart disease and diabetes.
                            But it also reduces the risk of cancer.”
                            """)
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
                    .content("""
                            Tyana Verstraete and Jiawei Xu, alumni of the School of Arts PXL-MAD, have been nominated for the Global Creative Graduate Showcase 2024 in collaboration with WGSN, Coloro, and Google Arts & Culture.
                            The public vote is officially open until Friday, 11 October. The winners will be announced on Monday, 14 October.
                            """)
                    .userId(3L)
                    .category(Category.ALUMNI)
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 8, 14, 56, 0))
                    .state(State.REJECTED)
                    .build();

            postRepository.save(post6);
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