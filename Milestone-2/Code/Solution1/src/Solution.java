import java.util.Map;

public class Solution {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Set<Integer> visited = new HashSet<>();
        int totalFriends = friends.length;
        for(int i = 0 ; i < totalFriends ; ++i){
            visited.add(i);
        }
        int friendCircles = 0;
        while(!visited.empty()){
            friendCircles++;
            int person = visited.iterator().next();
            dfs(visited, person, friends);
        }
	}
}
