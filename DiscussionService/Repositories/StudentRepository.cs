using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class StudentRepository : RepositoryBase<Student>, IStudentRepository
    {
        public StudentRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public void CreateStudent(Student student)
        {
            Create(student);
        }

        public void DeleteStudent(Student student)
        {
            Delete(student);
        }

        public async Task<IEnumerable<Student>> GetAllStudentsAsync()
        {
            return await FindAll().ToListAsync();

        }

        public async Task<Student> GetStudentByIdAsync(Guid studentId)
        {
            return await FindByCondition(student => student.Id.Equals(studentId)).FirstOrDefaultAsync();
        }

        public async Task<Student> GetStudentByEmail(string email)
        {
            return await FindByCondition(student => student.Email.Equals(email)).FirstOrDefaultAsync();
        }

        public void UpdateStudent(Student student)
        {
            Update(student);
        }
    }
}