import java.util.ArrayList;

/**
 * A class representing extremely large integers
 * Basic arithmetic operations can occur with BigInts
 */

public class BigInt {

    //instance variables
    private boolean sign;
    /**
     * <br>An array is used as opposed to a LinkedList due to memory efficiency
     * <br>In the middle of values will be the Integer to be held, digits held in each slot
     * <br>Each element holds a value between positive 0 and 999,999,999. While this does lose some
     * space
     */
    private int[] con;
    private int size; //the size of the array, not necessarily the number of digits
    private int start; //the integer is held in the middle of the starting list
    private int firstComplete; //number of digits in the first element

    //class constants
    final int DEFAULT_SIZE = 10;
    final int DIGITS_IN_ONE_BILLION = 9;
    final int MAX_IN_SLOT = 1_000_000_000;
    /**
     * Creates an empty BigInt object with a value of zero
     * <br>pre: none
     * <br> post: This object is set to empty
     */
    public BigInt(){
        sign = true;
        con = new int[DEFAULT_SIZE];
        start = con.length / 2;
        size = 0;
        firstComplete = 0;
    }

    /**
     * Creates a BigInt object that is represented by a String
     * <br> pre: num != null, num.length() > 0
     * <br> pre: num represents an integer
     * <br> post: This object represents an Integer equaling the String
     */
    public BigInt(String num){
        //pre-conditions
        if (num == null || num.isEmpty()){
            throw new IllegalArgumentException("String representation must not be null");
        }
        if (!StringIsInt(num)){
            throw new IllegalArgumentException("Invalid BigInt input: " + num);
        }
        int numSize = num.length() - 1;
        //account for negative ints
        int shift = 1;
        if (!(num.charAt(0) == '-')){
            sign = true;
            numSize++;
            shift--;
        }
        //initialize object
        size = numSize/DIGITS_IN_ONE_BILLION;
        start = DEFAULT_SIZE/2;
        con = new int[size + DEFAULT_SIZE];
        //sets value. Note that size is double-checked at this point
        int current = num.length();
        size = 0;
        while (current - DIGITS_IN_ONE_BILLION - shift >= 0){
            insertStart(Integer.parseInt
                    (num.substring(current - DIGITS_IN_ONE_BILLION, current)));
            current -= DIGITS_IN_ONE_BILLION;
            firstComplete = DIGITS_IN_ONE_BILLION;
        }
        if (current != shift){
            insertStart(Integer.parseInt(num.substring(shift, current)));
            firstComplete = current - shift;
        }
    }

    /**
     * Creates a new BigInt representing a long
     * <br> pre: none
     * <br> post: This object represents this long
     */
    public BigInt(Long l) {
        this(l.toString());
    }

    /**
     * Creates a new BigInt representing an int
     * <br> pre: none
     * <br> post: This object represents this int
     */
    public BigInt(Integer i){
        this(i.toString());
    }

    /**
     * Makes a new BigInt based off of a random number of digits
     * <br>pre: numDigits > 0 [checked in static method]
     * */
    public BigInt(long numDigits, boolean sign){
        this(makeRandom(numDigits));
        this.sign = sign;
    }

    /**
     * Gets the amount of digits in this BigInt object
     * <br>pre: none
     * <br> post: The amount of digits in this BigInt object
     */
    public int getNumDigits() {
        return DIGITS_IN_ONE_BILLION * (size - 1) + firstComplete;
    }

    /**
     * Returns a String value of this BigInt
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (!sign) {
            s.append("-");
        }
        if (size == 0) {
            return "0";
        }
        //handles first element
        s.append((con[start]));
        //only add extra zeros if needed
        for (int i = 1; i < size; i++) {
            String p = Integer.toString(con[start + i]);
            for (int j = DIGITS_IN_ONE_BILLION; j > p.length(); j--){
                s.append("0");
            }
            s.append(p);
        }
        return s.toString();
    }

    /**
     * Returns this int but with a specified number of digits
     * <br>pre: numDigits >= getNumDigits
     * <br>post: A string showing the int as a certain number of digits
     */
    public String printDigits(long numDigits) {
        //pre-conditions
        if (numDigits <= getNumDigits()){
            throw new IllegalArgumentException("Num digit representation cannot be less than" +
                    " the actual amount of digits");
        }
        StringBuilder toReturn = new StringBuilder();
        if (sign){
            toReturn.append(" ");
        } else {
            toReturn.append("-");
        }
        String num = this.toString();
        if (num.charAt(0) == '-'){
            num = num.substring(1);
        }
        for (long i = numDigits; i > num.length(); i--){
            toReturn.append(0);
        }
        toReturn.append(num);
        return toReturn.toString();
    }

    /**
     * Adds this BigInt to another BigInt
     * <br>pre: other != null
     * <br> "pre": The signs match (otherwise use subtraction)
     */
    public void add(BigInt other){
        //pre-condition
        if (other == null) {
            throw new IllegalArgumentException("Can't add with a null BigInt");
        }
        if (sign != other.sign) {
            throw new IllegalArgumentException("Can't add diff signs");
        }
        int carry = 0;
        //handles the currently set indices
        for (int i = 0; i < size; i++) {
            int sum = get(this, size - 1 - i) + get(other, other.size - 1 - i) + carry;
            carry = 0;
            while (sum >= Math.pow(10, DIGITS_IN_ONE_BILLION)) {
                carry++;
                sum -= (int)Math.pow(10, DIGITS_IN_ONE_BILLION);
            }
            con[size + start - 1 - i] = sum;
        }
        //in case there's more to other
        for (int i = size; i < other.size; i++) {
            int sum = get(other, other.size - 1 - i) + carry;
            carry = 0;
            while (sum >= Math.pow(10, DIGITS_IN_ONE_BILLION)) {
                carry++;
                sum -= (int)Math.pow(10, DIGITS_IN_ONE_BILLION);
            }
            insertStart(sum);
        }
        if (carry != 0) {
            insertStart(carry);
        }
    }

    /**
     * Multiplies this BigInt by another BigInt
     * <br>pre: other != null
     * <br>post: sets this BigInt to be times the other BigInt
     */
    public void multiply(BigInt other) {
        if (other == null) {
            throw new IllegalArgumentException("Can't have a null other");
        }
        BigInt temp = this.copy(); //unlike add, previous elements may be used again
        long carry = 0;
        this.clear();
        for (int i = 2; i < temp.size + other.size + 1; i++) {
            long newDigit = carry;
            carry = 0;
            for (int j = 1; j < i; j++) {
                //combines each base 1 billion digit
                newDigit += (long)get(temp, temp.size - (i-j)) *
                        (long)get(other, other.size - j);

                if (newDigit >= 8 * Math.pow(10, DIGITS_IN_ONE_BILLION * 2)) {
                    //prevents long overflow
                    carry += newDigit/MAX_IN_SLOT;
                    newDigit %= MAX_IN_SLOT;
                }
            }
            carry += newDigit/MAX_IN_SLOT;
            this.insertStart((int)(newDigit % MAX_IN_SLOT));
        }
        // extra digits
        while (carry >= MAX_IN_SLOT) {
            this.insertStart((int)(carry % MAX_IN_SLOT));
            carry /= MAX_IN_SLOT;
        }
        if (carry != 0) {
            this.insertStart((int)carry);
        }
        this.sign = (temp.sign == other.sign);
    }


    /**
     * Returns a copy of this BigInt
     * pre: none
     * post: A copy of this exact BigInt
     */
    public BigInt copy() {
        return new BigInt(this.toString());
    }

    /**
     * Wipes this BigInt clean
     */
    private void clear() {
        sign = true;
        con = new int[DEFAULT_SIZE];
        start = con.length / 2;
        size = 0;
        firstComplete = 0;
    }

    /**
     * "get" the ith element of the array
     * if out of bounds, returns 0, similar to how actual ints are
     * Useful for add methods
     * Already accounts for "start"
     * */
    private int get(BigInt num, int index) {
        if (index >= num.size) {
            return 0;
        }
        if (index < 0) {
            return 0;
        }

        return num.con[index + num.start];
    }

    /**
     * Inserts at the start
     * Note: Method made private as client has no idea to treat BigInt as an array
     */
    private void insertStart(int i) {
        if (i < 0 || i >= Math.pow(10 , DIGITS_IN_ONE_BILLION)){
            throw new IllegalArgumentException("An error occurred");
        }
        if (start == 0){
            resize();
        }
        start--;
        con[start] = i;
        size++;
        firstComplete = (int)Math.log10(i) + 1;
    }

    /**
     * Inserts at the end
     * Note: Method made private as client has no idea to treat BigInt as an array
     */
    private void insertEnd(int i) {
        if (i >= Math.pow(10 , DIGITS_IN_ONE_BILLION)){
            throw new IllegalArgumentException("An error occurred");
        }
        if (start + size == con.length - 1){
            resize();
        }
        con[start + size] = i;
        size++;

    }

    /**
     * Whenever we need to expand con, do this
     * We'll assume both sides need expansion
     * Proportional resize, much faster than linear
     */
    private void resize(){
        int[] biggerCon = new int[con.length * 2];
        int index = biggerCon.length / 4;
        if (size >= 0) {
            System.arraycopy(con, start, biggerCon, index, size);
        }
        con = biggerCon;
        start = con.length / 4;
    }

    /**
     * Helper method for constructor
     * Determines if num can be an int
     * <br> pre: checked by BigInt
     * <br> post: a boolean saying if num is an int
     */
    private boolean StringIsInt(String num){
        char c = num.charAt(0);
        final int ASCII_FOR_ZERO = 48;
        final int HIGHEST_DIGIT = 9;
        if (c != '-' && (((int)c - ASCII_FOR_ZERO < 0) ||
                ((int)c - ASCII_FOR_ZERO > HIGHEST_DIGIT))){
                    return false;
        }
        for (int i = 1; i < num.length(); i++){
            c = num.charAt(i);
            if ((((int)c - ASCII_FOR_ZERO < 0) || ((int)c - ASCII_FOR_ZERO > HIGHEST_DIGIT))){
                return false;
            }
        }
        return true;
    }

    /**
     * Makes a random String of numDigits digits
     * checks pre-condition from the BigInt consturctor used for this
     * */
    private static String makeRandom(long numDigits){
        if (numDigits <= 0){
            throw new IllegalArgumentException("Can't request a non-positive number of digits");
        }
        StringBuilder digits = new StringBuilder();
        for (long i = 0; i < numDigits; i++) {
            digits.append((char)(Math.random() * 10 + 48));
        }
        while (digits.charAt(0) == '0'){
            if (digits.length() != 1) {
                digits.deleteCharAt(0);
            }
            else {
                return "0";
            }
        }
        return digits.toString();
    }
}
