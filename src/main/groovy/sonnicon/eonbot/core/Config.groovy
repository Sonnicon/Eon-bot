package sonnicon.eonbot.core

class Config {
    String token = ""
    HashSet<Long> operators = []

    HashMap<String, ?> getMap(){
        ["token" : token, "operators" : operators]
    }
}
