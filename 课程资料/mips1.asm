.data
str_0: .asciiz ""
str_1: .asciiz "\n"
.text
	li $gp, 268435456
	li $fp, 268959744
	j main
	
main:
	li $v0, 5
	syscall
	move $t0, $v0
	sw $t0, 0($fp)
	lw $t0, 0($fp)
	li $t1, 4
	and $t2, $t0, $t1
	sw $t2, 0($fp)
	lw $t0, 0($fp)
	la $a0, str_0
	li $v0, 4
	syscall
	move $a0, $t0
	li $v0, 1
	syscall
	la $a0, str_1
	li $v0, 4
	syscall
	li $v0, 10
	syscall
