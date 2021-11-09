### Problem Statement

- Need to develop a Movie Theater seating assignement algorithm which can optimally allocate seats to the audience with required social distancing criteria.

### Theater seating rules

- The theater has a dimension 10 X 20. We need to allocate seats to people such that there is a **buffer** of **3 seats and 1 row** between the groups that visit the theatre.

### Input Description

The program will receive a file name as input. The file name should be passed as an aurgument to the command line while executing the program. The rows of the input file will have Request ID and the number of seats required seperated by a space.

| Request ID | Number of seats to book |
| ---------- | ----------------------- |
| R1         | 5                       |
| R2         | 12                      |
| R3         | 7                       |
| ....       | ....                    |

Output Description
The program should output a file which shows the request id and the corresponding seats assigned to that ID. In case there is no cluster with required number of seats, the ID should show "_Cluster of requested seats not available!_"

Sample output description:

| Request ID | Seats booked                              |
| ---------- | ----------------------------------------- |
| R1         | A1, A2, A3, A4                            |
| R2         | C1, C2, .... C12                          |
| R3         | E1, E2, ... E7                            |
| R4         | Cluster of requested seats not available! |
| ....       | ....                                      |

### Critical assumptions and considerations

- Each request wants to **get the tickets in a cluster**. For example, if the buyer wants 5 tickets, then he will only accept the tickets if they are consecutive in a row. If two different rows have 3 and 2 tickets available, we assume that the buyer won't be interested in buying the tickets.
- Buffer rule of 3 seats and 1 row needs to be strictly followed.
- Requests are processed in the **order in which they come**.
- Time complexity for selections and updation of seats must be **optimal**

### Algorithm to solve the problem:

1. Take file input from command line and read the content of the file
2. Create a seatCount Map which will represent the total number of seats filled up in each row.
3. Create a reservations map that will keep the list of seats assigned to each request.
4. Create a PriorityQueue to fetch the row with maximum number of empty seats. The comparator works by comparing the occupancy of each row, which is listed in seatcount. Using PriorityQueue, we can **fetch the row with maximum empty seats in log(n) time**.
5. Create a seatAvailable variable to map N number of empty seats are present in which rows. This is **reverse mapping** of what is stored by seatCount Map. **This Map is used so that we can get which row has exactly N empty seats in O(1) time.**
6. Start processing each request. Take out the row with maximum number of empty seats - O(log n)
7. If we find that there is some row having exact number of empty seats as requested by the user, take that row - O(1)
8. Take previous and next row of our current row (as we need to pad them to maintain social distancing)
9. Fill the empty seats in selected row and add padding to previous and next row, for the seat numbers selected in the current row.
10. Add 3 paddings to the current row, as we want to keep a buffer of 3 between people on same row.
11. Update the total number of occupied/empty seats in the current row, previous and next row through the maps - O(1)
12. If all the seats are showed as filled up, it might be the case that there is some empty seat in the middle of the rows. Check for that and fill if match is found - O(n^2)

## **Time Complexity:**

For finding seats for one request:

- Best case time complexity is **O(log n)**
- Worst case time complexity is **O(n^2)**

## Sample Input

R1 4 <br />
R2 6 <br />
R3 12 <br />
R4 16<br />
R5 10<br />
R6 11<br />
R7 90<br />
R8 3<br />
R9 5<br />
R10 4<br />
R11 3<br />
R12 1<br />
R13 1<br />

# Sample Output

R001 A1, A2, A3, A4

R002 C1, C2, C3, C4, C5, C6

R003 E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12

R004 G1, G2, G3, G4, G5, G6, G7, G8, G9, G10, G11, G12, G13, G14, G15, G16

R005 I1, I2, I3, I4, I5, I6, I7, I8, I9, I10

R006 C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20

R007 Cluster 0f Requested seats not available!

R008 A8, A9, A10

R009 E16, E17, E18, E19, E20

R0010 H17, H18, H19, H20

R0011 J11, J12, J13

R0012 A14

R0013 J17
