**OVERVIEW**

A partially complete implementation of a Big Integer, an object that is meant to represent whole numbers that far exceed the standard Java Integer or Long limits.

Includes operations such as same sign addition and multiplication (coming later: Subtraction/Different sign addition, integer division, and exponentation)

**IMPLEMENTATION**

The number is stored as an Array of primtive ints

- An ArrayList is not used as several operations require inserting at the start of the list, which is highly inefficent
  - Furthermore, the Integer class takes up far more storage than a primitive int
- For this same reason, a LinkedList is not used (far more storage than neccessary)
  - Leads to inserting at the start and end having amoritized O(1) time rather than pure O(1), which is acceptable

The Array itself contains the "digits" of the BigInt 9 at a time, so for example: "1121231234123451234561234567" would be stored as
- [000000001 , 121231234 , 123451234 , 561234567]
  - With the amount of digits in the first index being stored to prevent toString errors
  - While this does cause a slight waste in memory (each 32 bit int can only hold 1,000,000,000 values, about a fourth of what it could do). However, this implementation makes converting a BigInt to String (and vice versa) much easier
  - Longs are not stored due to the multiplication operation, which requires multiplying two longs (can lead to overflow/impresion as no primitive type goes beyond long except doubles)

**PUBLIC METHODS**

    /**
     * Creates a BigInt object that is represented by a String
     * <br> pre: num != null, num.length() > 0
     * <br> pre: num represents an integer
     * <br> post: This object represents an Integer equaling the String
     * @param num the number to be converted
     */
    public BigInt(String num){}

        /**
     * Creates a new BigInt representing a long
     * <br> pre: none
     * <br> post: This object represents this long
     * @param l the long to be converted to a BigInt
     */
    public BigInt(Long l) {
        this(l.toString());
    }

    /**
     * Creates a new BigInt representing an int
     * <br> pre: none
     * <br> post: This object represents this int
     * @param i the integer to be converted to a BigInt
     */
    public BigInt(Integer i){
        this(i.toString());
    }

    /**
     * Makes a new BigInt based off of a random number of digits
     * <br>pre: numDigits > 0 [checked in static method]
     * @param numDigits the number of digits this BigInt is allowed to have
     * @param sign the sign of the BigInt (true = positive)
     * */
    public BigInt(long numDigits, boolean sign){

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
    public String toString() {}

    /**
     * Returns this int but with a specified number of digits
     * <br>pre: numDigits >= getNumDigits
     * <br>post: A string showing the int as a certain number of digits
     * @param numDigits the number of digits to print out
     */
    public String printDigits(long numDigits) {}

    /**
     * Adds this BigInt to another BigInt
     * <br>pre: other != null
     * <br> "pre": The signs match (otherwise use subtraction)
     * @param other the other BigInt
     */
    public void add(BigInt other){}

    /**
     * Multiplies this BigInt by another BigInt
     * <br>pre: other != null
     * <br>post: sets this BigInt to be times the other BigInt
     * @param other the other BigInt
     */
    public void multiply(BigInt other) {}
  
    /**
     * Returns a deep copy of this BigInt
     * pre: none
     * post: A deep copy of this exact BigInt
     */
    public BigInt copy() {
      return new BigInt(this.toString());
    }

**USE CASES**

Oftentimes, combinatoric problems require numbers far greater than what an Integer, Long, or even a Double can hold. With this, whole numbers of nearly unlimited size can be made
- Of course, limitations on computer memory still apply

**EXAMPLE CODE**

    /**
    * Generates all the factorials up to num
    */
    public static void factorial(int num) {
        BigInt i = new BigInt(1);
        for (int j = 2; j <= num; j++) {
            i.multiply(new BigInt(j));
            System.out.println("\n" + j + " factorial is " + i);
        }
    }








  
