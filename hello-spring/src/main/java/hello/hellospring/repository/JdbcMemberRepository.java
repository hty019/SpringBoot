package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

import static java.sql.DriverManager.getConnection;

public class JdbcMemberRepository implements MemberRepository{

    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            conn=getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//RETURN_GENERATED_KEYS는 SQL문이 실행됐을 때,
                                                                                //내부에서 생성되는 키값을 받아온다.PK값

            pstmt.setString(1,member.getName());

            pstmt.executeUpdate();
            rs=pstmt.getGeneratedKeys();//발생한 키값을 받아온다.

            if(rs.next()){
                member.setId(rs.getLong(1));
            }else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        }catch (Exception e) {
            throw new IllegalStateException(e);
        }finally {
            close(conn,pstmt,rs);
        }
        return null;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Member> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<Member> findAll() {
        return null;
    }

    @Override
    public void clearStore() {

    }
}
