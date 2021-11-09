import java.util.*;
import javax.naming.spi.ResolveResult;
import javax.sound.sampled.BooleanControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.io.FileWriter;
import java.io.IOException;

class MovieSeats2{


    public static boolean checkRange(char[][] cinema, int row, int start, int end){
        for(int i = start; i <= end; i++){
            if(cinema[row][i] != '.') return false;
        }

        return true;
    }

    // Method to remove currently selected seat.
    public static void removeFromSet(HashMap<Integer, HashSet<Character>> seatAvailable, int count, char ch){
            if(count==0) return ;
            HashSet<Character> hs;
            hs = seatAvailable.get(count);
            hs.remove(ch);
            if(hs.size()==0){
                seatAvailable.remove(count);
                return ;
            } 
            seatAvailable.put(count, hs);
    }

    // Method to add currently updated seat
    public static void addToSet(HashMap<Integer, HashSet<Character>> seatAvailable, int count, char ch){
            HashSet<Character> hs = seatAvailable.getOrDefault(count, new HashSet<>());
            hs.add(ch);
            seatAvailable.put(count, hs);
    }
    public static void main(String[] args) {
        
            // Create a file object with argument parameter passed as the file name.
            File ticketRequests = new File(args[0]);

            // Create a file object to store 
            File ticketAllocation = new File("ticketApproved.txt");

            // Create a matrix for visual representation of seating arrangments
            char [][]cinema = new char[10][20];

            // Fill the matrix with dot, representing empty seat. 
            for(char cin[] : cinema){
                Arrays.fill(cin, '.');
            }

            ArrayList<String[]> requests = new ArrayList<>();

            // Read the input from the file.
            try{
                Scanner sc = new Scanner(ticketRequests);
                while(sc.hasNextLine()){
                    String req[]=sc.nextLine().split(" ");
                    requests.add(req); 
                }
            }
            catch (FileNotFoundException  ex){
                System.out.println("File not found!");
            }


            // Seat count map will represent the total number of seats filled up in each row
            HashMap<Character, Integer> seatCount = new HashMap<>();

            // Reservations map will keep the list of seats assigned to each request
            LinkedHashMap<String, ArrayList<String>> reservations = new LinkedHashMap<>();

            // Initially all rows are empty so set the value as 0
            for(char c = 'A'; c <= 'J'; c++){
                seatCount.put(c, 0);
            }

            // PriorityQueue to fetch the row with maximum number of empty seats. The comparator works by comparing the occupancy of each row, which is listed in seatcount. 
            // Using PQ, we can fetch the row with maximum empty seats in log(n) time.
            PriorityQueue<Character> pq = new PriorityQueue<>(new Comparator<Character>() {
                public int compare(Character a, Character b){
                    if(seatCount.get(a) == seatCount.get(b)){
                        return a - b; 
                    }

                    return seatCount.get(a) - seatCount.get(b);
                }
            });

            // Add all rows to PriorityQueue
            for(char c = 'A'; c <= 'J'; c++){
                pq.add(c);
            }

            /* 
            seatAvailable variable to map N number of empty seats are present in which rows.
            This is reverse mapping of what is stored by seatCount Map.
            This Map is used so that we can get which row has exactly N empty seats in O(1) time. 
            */
            HashMap<Integer, HashSet<Character>> seatAvailable = new HashMap<>();

            HashSet<Character> hs = new HashSet<>();

            for(char z = 'A'; z<='J'; z++) hs.add(z);

            // Initially all rows have 20 empty seats 
            seatAvailable.put(20, hs);

            // Assign seats for each request one by one.
            for(int i = 0; i < requests.size(); i++){
                String request[] = requests.get(i);

                // Check for invalid input
                if(request.length!=2){
                    System.out.println("Request number "+ (i+1) + " is invalid!");
                    continue;
                }

                String id = request[0];
                int booking = Integer.parseInt(request[1]);

                if(booking < 1 && booking > 200){
                    System.out.println("Request number "+ (i+1) + " is invalid!");
                    continue;
                }

                if(!reservations.containsKey(id)){
                    reservations.put(id, new ArrayList<String>());
                }
                

                // Take out the row with maximum number of empty seats - O(log n)
                char row = pq.poll();
                int seatsFilled = seatCount.get(row);
                int seatsAvailable = 20 - seatsFilled;

                // If we find that there is some row having exact number of empty seats as requested by the user, take that row - O(1) 
                if(seatAvailable.containsKey(booking)){
                    HashSet<Character> x = seatAvailable.get(booking);
                    for(char ch : x) {
                        pq.add(row);
                        row = ch;
                        seatsFilled = seatCount.get(row);
                        seatsAvailable=booking;
                        break;
                    }
                }

                if(booking <= seatsAvailable){
                    ArrayList<String> al = reservations.get(id);

                    // Take previous and next row of our current row
                    char prev = (char)(row-1), next = (char)(row+1);

                    removeFromSet(seatAvailable, 20 - seatCount.get(row), row);

                    if(prev >= 'A' && prev <= 'J'){
                        pq.remove(prev);
                        removeFromSet(seatAvailable, 20 - seatCount.get(prev), prev);
                    }


                    if(next >= 'A' && next <= 'J'){
                        pq.remove(next);
                        removeFromSet(seatAvailable, 20 - seatCount.get(next), next);
                    }

                    for(int z = seatsFilled + 1; z <= seatsFilled + booking; z++){
                        
                        // Fill the empty seats in selected row.
                        al.add(Character.toString(row) + String.valueOf(z));
                        
                        cinema[row-'A'][z - 1] = 'X';

                        // As we are keeping distance of one row between people, adding padding in the previous and next row.
                        if(prev >= 'A' && prev <= 'J'){
                            seatCount.put(prev, Math.max(seatCount.get(prev), z));
                            cinema[prev - 'A'][z-1] = '-';
                        }
                        if(next >= 'A' && next<='J'){
                            seatCount.put(next, Math.max(seatCount.get(next), z));
                            cinema[next - 'A'][z-1] = '-';
                        }
                    }

                    // Add 3 paddings to the current row, as we want to keep a buffer of 3 between people on same row
                    for(int z = seatsFilled + booking + 1; z <= Math.min(seatsFilled + booking + 3, 20); z++){
                        cinema[row-'A'][z - 1] = '-';
                    }

                    // Put the selected seats against the request id
                    reservations.put(id, al);
                    

                    seatsFilled += (booking + 3);
                    booking = 0;

                    // Update the total number of occupied seats in the current row, previous and next row - O(1)
                    seatCount.put(row, Math.min(seatsFilled, 20));
                    
                    if(seatCount.get(row) < 20){
                        pq.add(row);
                        addToSet(seatAvailable, 20 - seatCount.get(row), row);
                    }

                    if((prev >= 'A' && prev <= 'J') && seatCount.get(prev) < 20){
                        pq.add(prev);
                        addToSet(seatAvailable, 20 - seatCount.get(prev), prev);
                    }

                    if((next >= 'A' && next <= 'J') && seatCount.get(next) < 20){
                        pq.add(next);
                        addToSet(seatAvailable, 20 - seatCount.get(next), next);
                    }

                }

                else pq.add(row);
    
                if(booking!=0){

                    // If all the seats are showed as filled up, it might be the case that there is some empty seat in the middle of the row. Check for that and fill if match is found - O(n^2)  
                    ArrayList<String> al = reservations.get(id);
                    
                    for(char j = 'A'; j <= 'J'; j++){
                        for(int k = 0; k < 20; k++){

                            // cinema[j - 'A'][k]=='.' && cinema[j - 'A'][k + booking -1]=='.'
                            if(booking + k - 1< 20 && checkRange(cinema, j - 'A', k, k + booking -1)){
                                for(int z = k; z < booking + k; z++){
                                    cinema[j - 'A'][z] = 'X';
                                    al.add(Character.toString(j) + String.valueOf(z));
                                    if((char)(j-1) >= 'A' && (char)(j-1) >= 'J') cinema[(char)(j-1) - 'A'][z] = '-';
                                    if((char)(j+1) >= 'A' && (char)(j+1) <= 'J') cinema[(char)(j+1) - 'A'][z] = '-';
                                }
                                for(int z=booking + k; z < Math.min(booking+ k +3, 20); z++){
                                    cinema[j - 'A'][z] = '-';
                                }
                                if(al.size()>0) break;
                            }
                        }
                        if(al.size()>0) break;
                    }
                }
                
                if(pq.size()==0){
                    System.out.println("No more seats available!");
                    break;
                }

            }

            try{
                FileWriter fw = new FileWriter(ticketAllocation, false);
                for(String id : reservations.keySet()){
                    ArrayList<String> seats = reservations.get(id);

                    fw.write(id + "\t");
                    if(seats.size()==0){
                        fw.write("Cluster of requested seats not available!" + "\n\n");
                        continue;
                    }

                    for(int z= 0; z < seats.size(); z++){
                        fw.write(seats.get(z));
                        if(z!=seats.size()-1) fw.write("," + " ");
                    }

                    fw.write("\n\n");
                }

                fw.write("\n\n");

                for(char []cin : cinema){
                    for(char z : cin){
                        fw.write(z + " ");
                    }
                    fw.write("\n");
                }

                fw.close();
                System.out.println("The ticket's list can be found at => "+ ticketAllocation.getAbsolutePath());
            }
            catch (IOException ex){
                System.out.println("Exception ocured while writing the file!");
            }

    }
}