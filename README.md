[![Dynamic JSON Badge](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fraw.githubusercontent.com%2FSwEnt-Group8%2Fcoverage-line-scrapper%2Frefs%2Fheads%2Fmain%2Fparsed-coverage.json&query=LineCoverage&logo=sonarcloud&label=Coverage%20line&color=0ca50c&link=https%3A%2F%2Fsonarcloud.io%2Fcomponent_measures%3Fmetric%3Dline_coverage%26view%3Dlist%26id%3DSwEnt-Group8_Street-work-app)](https://sonarcloud.io/component_measures?metric=line_coverage&view=list&id=SwEnt-Group8_Street-work-app)
[![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=SwEnt-Group8_Street-work-app&metric=bugs)](https://sonarcloud.io/summary/overall?id=SwEnt-Group8_Street-work-app)
[![SonarCloud Security](https://sonarcloud.io/api/project_badges/measure?project=SwEnt-Group8_Street-work-app&metric=security_rating)](https://sonarcloud.io/summary/overall?id=SwEnt-Group8_Street-work-app)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=SwEnt-Group8_Street-work-app&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=SwEnt-Group8_Street-work-app)
[![SonarCloud Reliability](https://sonarcloud.io/api/project_badges/measure?project=SwEnt-Group8_Street-work-app&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=SwEnt-Group8_Street-work-app)

# The Street Work’App – Bringing Athletes Together

## Visual

To see the complete design of our app [Figma 🎨](https://www.figma.com/design/4mU3E7xxGna8ou16wqOwAO/Street-Work'App?node-id=0-1&t=1wSLdlQFebCPNyh4-1)

## Team Members

| Name                                | Github profile                              |
|-------------------------------------|---------------------------------------------|
| Alexandre Paul Auguste Norén Vaisse | [Alex720p](https://github.com/Alex720p)     |
| Alvaro Julien Moya Mendez           | [alvaro1080](https://github.com/alvaro1080) |
| Arthur Muster                       | [SaturneV](https://github.com/SaturneV)     |
| Kemin Zheng                         | [kzepfl](https://github.com/kzepfl)         |
| Malick Alexandre Kodjo Sy           | [misterM125](https://github.com/misterM125) |
| Paul Tercier                        | [tercierp](https://github.com/tercierp)     |
| Simon Schranz                       | [Simmanz](https://github.com/Simmanz)       |


## Overview
The Street Work'App is designed to connect street workout enthusiasts by recommending parks for workouts and facilitating real-life social interactions. Street workout is a growing physical activity that combines calisthenics and gymnastics, performed in outdoor spaces like parks. This app helps users find like-minded individuals to practice with and promotes the development of a local street workout community.

## Project architecture
<img width="872" alt="image" src="https://github.com/user-attachments/assets/55dcd946-d8ae-48da-a5df-3b9154bca342">


## Core Features
1. **Park Discovery via Location:**
    - Users can discover nearby parks for street workouts based on their current location.

2. **Friendship Through In-Person Connections:**
    - Friends can only be added by meeting at the same park with bluetooth, encouraging real-world connections.

3. **Park Events:**
    - Users can create and join events at parks to meet others and explore new workout locations.
   
4. **Progression**
    - The app includes an achievement and progression system where users can track their milestones, unlock badges for completing specific goals. This gamification feature motivates users by rewarding consistent effort and providing a visual representation of their progress over time.
   
5. **Training and Exercise Management**
    - The app allows users to choose between training solo or with a friend acting as a coach, offering a variety of exercises such as Push-ups, Dips, Burpees, Lunges, Planks, Handstands, Front Lever, Flag, and Muscle-ups, enabling a customizable workout experience.

## Secondary Features
- **Gamification:**
    - Users can earn levels, badges, and stats, rewarding consistent park visits and social connections.

- **Park Ratings and Reviews:**
    - Users can review parks based on criteria like practicality, friendliness, and aesthetics, helping others discover the best spots.

## Advanced Feature (Future Upgrade)
- **Body Pose Tracking:**
    - The app could integrate body pose tracking to help users analyze their performance on street workout figures like handstands or front levers.

## Technical Requirements
- **Firebase Integration:**
    - Parks and profiles are stored using Google Firebase.

- **Location Services:**
    - The app uses GPS for park recommendations via the Google Maps API.

- **Offline Mode:**
    - Users can view cached maps and workout plans even without an internet connection.

- **Bluetooth Integration:**
    - Enable seamless pairing between users for friend requests and collaboration during workouts.
      
- **Image Uploading:**
    - Utilize Digital Ocean via AWS S3 API for uploading and storing park images and user profile pictures efficiently.
      
- **Text Moderation:**
    - Integrate the Perspective API to moderate user-generated text content, ensuring a safe and respectful community.

The Street Work'App aims to not only provide workout locations but also to build lasting connections between street workout enthusiasts in their communities.
