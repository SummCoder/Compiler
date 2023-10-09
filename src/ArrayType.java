public class ArrayType implements Type {
    int count;
    Type subType;
    public ArrayType(int count, Type subType) {
        this.count = count;
        this.subType = subType;
    }
    @Override
    public String toString() {
        return "array(" + subType + ")";
    }
}