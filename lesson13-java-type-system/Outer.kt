    class Outer(val name: String) {
        // class B {     // <=> Nested Class ~ static nested class
        inner class B {  // <=> Inner class  ~ non-static nested class
            fun print() {
                // println("Outer name of A is " + name)
                println("Outer name of A is " + this@Outer.name)
            }
        }
    }