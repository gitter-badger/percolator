(ns percolator.core)
(declare expression-interpreter-for-form interpret-expression-again-or-identity interpret-statement-again-or-identity expression-interpreters interpret-declarator eval-and-interpret interpret-expression-binary-operation split-arguments-and-body-decls interpret-expression-this interpret-expression-variable-declaration interpret-expression-new interpret-expression-super japaparser-operator-type-unary interpret-expression-ambiguous-binary-or-unary-operation interpret-expression-assignment-operation japaparser-operator-constant interpret-expression interpret-expression-unary-operation interpret-expression-method-call
primitive-type interpret-type reference-type
first-form-that-looks-like interpret-modifiers modifiers-keywords partition-by-starts-with
interpret-statement-do-while interpret-block interpret-statement-throw interpret-statement-for interpret-statement-foreach interpret-statement interpret-switch-entry-statement interpret-statement-if interpret-statement-while interpret-statement-switch interpret-statement-break interpret-statement-return statement-interpreters interpret-statement-continue
is-class-modifier-option interpret-class-modifier-option interpret-body-decl-ctor body-decl-interpreters interpret-body-decl-method interpret-body-decl-class interpret-body-decl interpret-body-decl-field interpret-parameter snip-class-modifier-options-from-body-decls interpret-class-modifier-options
vomit-class-decl return-false add-two-to-s wrap-a-class-kluge)

(ns percolator.core
  (:require [clojure.contrib.string :as string])
  (:import
    (japa.parser.ast.body AnnotationDeclaration 
                          AnnotationMemberDeclaration 
                          ClassOrInterfaceDeclaration 
                          ConstructorDeclaration 
                          EmptyMemberDeclaration 
                          EmptyTypeDeclaration 
                          EnumConstantDeclaration 
                          EnumDeclaration 
                          FieldDeclaration 
                          InitializerDeclaration 
                          MethodDeclaration
                          TypeDeclaration ; I think this is an abstract base
                          ModifierSet     ; that's like public and private and static and abstract and synchronized and final and all that shit
                          Parameter       ; as in a method declaration
                          VariableDeclarator
                          VariableDeclaratorId
                          )
(japa.parser ASTHelper)
(japa.parser.ast CompilationUnit
                 PackageDeclaration
                 ImportDeclaration)
(japa.parser.ast.stmt AssertStmt                            ; NOTYET
                               BlockStmt                             ; used, perhaps need a syntax for anonymous blocks
                               BreakStmt                             ; done, doesn't support identifying them uniquely which I think is only useful if you're using javaparser for modifying existing ASTs
                               ContinueStmt                          ; done, ditto unique identification
                               DoStmt                                ; done
                               EmptyStmt                             ; maybe not needed?
                               ExplicitConstructorInvocationStmt     ; TODO might kinda require class & method declarations 
                               ExpressionStmt                        ; done
                               ForeachStmt                           ; done
                               ForStmt                               ; done-ish doesn't support multiple expressions in initializer or updater
                               IfStmt                                ; done
                               LabeledStmt                           ; TODO think of a syntax for this
                               ReturnStmt                            ; done
                               SwitchEntryStmt                       ; done
                               SwitchStmt                            ; done
                               SynchronizedStmt                      ; NOTYET
                               ThrowStmt                             ; done
                               TryStmt                               ; NOTYET
                               TypeDeclarationStmt                   ; done
                               WhileStmt                             ; done
                               )
(japa.parser.ast.expr AnnotationExpr
                               ArrayAccessExpr 
                               ArrayCreationExpr 
                               ArrayInitializerExpr 
                               AssignExpr                       ; done
                               AssignExpr$Operator              ; done
                               BinaryExpr                       ; done
                               BinaryExpr$Operator              ; done
                               BooleanLiteralExpr               ; done
                               CastExpr 
                               CharLiteralExpr                  ; done
                               ClassExpr                        ; done
                               ConditionalExpr                  ; aka ternary TODO do if statement first
                               DoubleLiteralExpr                ; done
                               EnclosedExpr                     ; wtf
                               FieldAccessExpr                  ; done-ish with special /-in-a-symbol syntax (only possible if target is a NameExpr)
                               InstanceOfExpr 
                               IntegerLiteralExpr               ; not possible everything is a long, no biggie
                               IntegerLiteralMinValueExpr       ; wtf
                               LiteralExpr                      ; abstract
                               LongLiteralExpr                  ; done-ish ... better type inference from clojure primitives
                               LongLiteralMinValueExpr          ; wtf
                               MarkerAnnotationExpr 
                               MethodCallExpr                   ; done (anything missing?)
                               NameExpr                         ; done
                               NormalAnnotationExpr             ; wtf
                               NullLiteralExpr                  ; done
                               ObjectCreationExpr               ; done-ish, doesn't support outer/inner classes
                               QualifiedNameExpr                ; done-ish with the /-in-a-symbol syntax
                               SingleMemberAnnotationExpr 
                               StringLiteralExpr                ; done
                               SuperExpr                        ; done
                               ThisExpr                         ; done
                               UnaryExpr                        ; FIXME TODO
                               UnaryExpr$Operator               ; done
                               VariableDeclarationExpr          ; done-ish? can do a simple local variable ... lacking array types
                               )
( japa.parser.ast.type ClassOrInterfaceType 
                                PrimitiveType         ; a degrading term
                                PrimitiveType$Primitive
                                ReferenceType 
                                Type
                                VoidType              ; dude is going to build void rays
                                WildcardType )
    )

  )

(load-file "/home/blake/w/percolator/src/percolator/util.clj")
(load-file "/home/blake/w/percolator/src/percolator/declaration.clj")
(load-file "/home/blake/w/percolator/src/percolator/expression.clj")
(load-file "/home/blake/w/percolator/src/percolator/japaparser.clj")
(load-file "/home/blake/w/percolator/src/percolator/statement.clj")
(load-file "/home/blake/w/percolator/src/percolator/type.clj")
(load-file "/home/blake/w/percolator/src/percolator/extension.clj")

(defmacro wrap-a-class-kluge [package-decl import-decls class-decl]
  `(new CompilationUnit
    ~(interpret-package-declaration package-decl)
    [~@(map interpret-import-decl import-decls)]
    [~class-decl]
    [] ;comments
    ))

(defmacro class-decl [& args]
  (apply interpret-body-decl-class args))
