{pkgs ? import <nixpkgs> {}}:
pkgs.mkShell {
  buildInputs = [
    pkgs.jdk21
    pkgs.fontconfig
    pkgs.freetype
    pkgs.glib
  ];

  shellHook = ''
    export JAVA_HOME=${pkgs.jdk21}
    export PATH=$JAVA_HOME/bin:$PATH
  '';
}
